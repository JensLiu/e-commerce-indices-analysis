package dev.jens.utils

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.TopicPartition
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent

object MyKafkaUtils {

    private val properties = MyPropertyUtils.load("config.properties")
    private val broker_list = properties.getProperty("kafka.broker.list")

    // kafka consumer configuration
    private val kafkaParams = collection.mutable.Map[String, Object](
        "bootstrap.servers" -> broker_list,
        "group.id" -> "consumer-group",
        "key.deserializer" -> classOf[org.apache.kafka.common.serialization.StringDeserializer],
        "value.deserializer" -> classOf[org.apache.kafka.common.serialization.StringDeserializer],
        "auto.offset.reset" -> "latest",
        "enable.auto.commit" -> (true: java.lang.Boolean)
    )

    // create DStream
    // add consumer group
    def createKafkaStream(streamingContext: StreamingContext,
                          topic: String,
                          groupId: String,
                          offset:Map[TopicPartition, Long]): InputDStream[ConsumerRecord[String, String]] = {
        kafkaParams("group.id") = groupId
        val kafkaStream = KafkaUtils.createDirectStream[String, String](
            streamingContext,
            PreferConsistent,
            Subscribe[String, String](Array(topic), kafkaParams, offset)
        )
        kafkaStream
    }

//    def getKafkaDStream(topic: String, groupId: String, )

}
