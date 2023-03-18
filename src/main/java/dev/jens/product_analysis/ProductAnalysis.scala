package dev.jens.product_analysis

import com.alibaba.fastjson.{JSON, JSONArray, JSONObject}
import dev.jens.enums.{MyRegion, ProductActivityType}
import dev.jens.utils.{MyESUtils, MyKafkaUtils}
import java.text.SimpleDateFormat
import java.util.Date
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.{Seconds, StreamingContext}
import scala.collection.mutable.ListBuffer

object ProductAnalysis {

    private val PRODUCT_ANALYSIS_INDEX_PREFIX = "product_analysis"
    private val SYSTEM_RECOMMENDATION_ANALYSIS_INDEX_PREFIX = "system_recommendation_analysis"

    val appLogic: InputDStream[ConsumerRecord[String, String]] => Unit = kafkaStream => {

        val domainStream = kafkaStream.map(_.value()).flatMap(DataParser.parseJsonToDomain)

        domainStream.foreachRDD(rdd => rdd.foreachPartition(itr => {

            val productivityActivityDtoList = new ListBuffer[ProductClickThroughRateDto]
            val systemRecommendationAnalysisDtoList = new ListBuffer[SystemRecommendationAnalysisDto]
            transformData(itr, productivityActivityDtoList, systemRecommendationAnalysisDtoList)

            val date = new SimpleDateFormat("yyyy-MM-dd").format(new Date())

            MyESUtils.bulkInsert(productivityActivityDtoList.toList,
                PRODUCT_ANALYSIS_INDEX_PREFIX + "_" + date)

            MyESUtils.bulkInsert(systemRecommendationAnalysisDtoList.toList,
                SYSTEM_RECOMMENDATION_ANALYSIS_INDEX_PREFIX + "_" + date)

        }))

    }
    private def transformData(itr: Iterator[ProductActivityDomain],
                              productActivityDtoList: ListBuffer[ProductClickThroughRateDto],
                              systemRecommendationAnalysisDtoList: ListBuffer[SystemRecommendationAnalysisDto]): Unit = {

        for (record: ProductActivityDomain <- itr) {
            val pDto = new ProductClickThroughRateDto(record)
            productActivityDtoList.append(pDto)

            val sDto = SystemRecommendationAnalysisDto.fromDomain(record)
            if (sDto != null) {
                systemRecommendationAnalysisDtoList.append(sDto)
            }
        }
    }

    def main(args: Array[String]): Unit = {
        execute(appLogic)
    }

    def execute(executeLogic: InputDStream[ConsumerRecord[String, String]] => Unit): Unit = {
        val streamingContext: StreamingContext = new StreamingContext("local[3]", "promotion_purchase_ratio", Seconds(3))
        streamingContext.sparkContext.setLogLevel("WARN")
        val kafkaStream = MyKafkaUtils.createKafkaStream(
            streamingContext = streamingContext,
            topic = "event",
            groupId = "consumer-group",
            offset = Map()
        )

        executeLogic(kafkaStream)

        streamingContext.start()
        streamingContext.awaitTermination()
    }


}
