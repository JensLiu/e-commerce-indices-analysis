package dev.jens.user

import dev.jens.entity.UserStartRecordMapper
import dev.jens.utils.{MyESUtils, MyKafkaUtils, MyRedisUtils}
import java.text.SimpleDateFormat
import java.util.Date
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.{Seconds, StreamingContext}
import scala.collection.mutable.ListBuffer

object DailyActiveUserAnalysis {

    val DAILY_ACTIVE_USER_INDEX_PREFIX = "active_user_analysis"

    def main(args: Array[String]): Unit = {
        execute(appLogic)
    }

    def execute(executeLogic: InputDStream[ConsumerRecord[String, String]] => Unit): Unit = {
        // 创建流
        val streamingContext: StreamingContext = new StreamingContext("local[3]", "daily_active_user", Seconds(3))
        val kafkaStream = MyKafkaUtils.createKafkaStream(
            streamingContext = streamingContext,
            topic = "start",
            groupId = "consumer-group",
            offset = Map()
        )
        // 执行逻辑建立
        executeLogic(kafkaStream)
        // 启动流
        streamingContext.start()
        streamingContext.awaitTermination()
    }


    val appLogic: InputDStream[ConsumerRecord[String, String]] => Unit = kafkaStream => {
        val startRecordStream = kafkaStream.map(_.value()).map(UserStartRecordMapper.toDto)

        val uniqueStartRecordStream = startRecordStream.mapPartitions(filterDuplicates)

        uniqueStartRecordStream.foreachRDD(rdd => {
            rdd.foreachPartition(itr => {
                val date = new SimpleDateFormat("yyyy-MM-dd").format(new Date())
                MyESUtils.bulkInsert(itr.map(r => (r.getUserId, r))
                        .toList, DAILY_ACTIVE_USER_INDEX_PREFIX + "_" + date)
            })
        })

    }

    private val filterDuplicates: Iterator[UserStartRecordDto] => Iterator[UserStartRecordDto] = itr => {
        val client = MyRedisUtils.getJedisClient()
        val tempList = new ListBuffer[UserStartRecordDto]
        for (record: UserStartRecordDto <- itr) {
            val date = record.getDate
            val setKey = s"daily_active_user:$date"
            val setElem = record.getDeviceId
            val rtnVal = client.sadd(setKey, setElem)
            if (client.ttl(setKey) < 0) {
                client.expire(setKey, 60 * 60 * 24)
            }
            if (rtnVal == 1L) {
                tempList.append(record)
            }
        }
        client.close()

        tempList.toIterator
    }

}
