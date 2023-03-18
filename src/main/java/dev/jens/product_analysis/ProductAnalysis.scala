package dev.jens.product

import com.alibaba.fastjson.{JSON, JSONArray, JSONObject}
import dev.jens.Region
import dev.jens.utils.{MyDataParserUtils, MyESUtils, MyKafkaUtils}
import java.text.SimpleDateFormat
import java.util.Date
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.parquet.format.PageType
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.{Seconds, StreamingContext}
import scala.collection.mutable.ListBuffer

object ProductAnalysis {

    private val PRODUCT_ANALYSIS_INDEX_PREFIX = "product_analysis"
    private val SYSTEM_RECOMMENDATION_ANALYSIS_INDEX_PREFIX = "system_recommendation_analysis"

    val appLogic: InputDStream[ConsumerRecord[String, String]] => Unit = kafkaStream => {

        val domainStream = kafkaStream.map(_.value()).flatMap(parseToDomain)

        domainStream.foreachRDD(rdd => rdd.foreachPartition(itr => {

            var productivityActivityDtoList = new ListBuffer[ProductActivityDto]
            var systemRecommendationAnalysisDtoList = new ListBuffer[SystemRecommendationAnalysisDto]
            saperateData(itr, productivityActivityDtoList, systemRecommendationAnalysisDtoList)

            val date = new SimpleDateFormat("yyyy-MM-dd").format(new Date())

            MyESUtils.bulkInsert(productivityActivityDtoList.map(
                (record: ProductActivityDto) => (null, record)
            ).toList, PRODUCT_ANALYSIS_INDEX_PREFIX + "_" + date)

            MyESUtils.bulkInsert(systemRecommendationAnalysisDtoList.map(
                (record: SystemRecommendationAnalysisDto) => (null, record)
            ).toList, SYSTEM_RECOMMENDATION_ANALYSIS_INDEX_PREFIX + "_" + date)

        }))

    }
    def saperateData(itr: Iterator[ProductActivityDomain],
                  productActivityDtoList: ListBuffer[ProductActivityDto],
                  systemRecommendationAnalysisDtoList: ListBuffer[SystemRecommendationAnalysisDto]): Unit = {

        for (record: ProductActivityDomain <- itr) {
            val pDto = new ProductActivityDto(record)
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

    def parseToDomain(jsonString: String): Iterable[ProductActivityDomain] = {
        // json 转换成 domain
        val itemActivityList = new ListBuffer[ProductActivityDomain]

        val jsonObject = JSON.parseObject(jsonString)

        val common: JSONObject = jsonObject.getJSONObject("common")
        val displays: JSONArray = jsonObject.getJSONArray("displays")
        val page: JSONObject = jsonObject.getJSONObject("page")
        val timestamp: Long = jsonObject.getLong("ts")
        var region: Region = null

        if (common != null) {
            val regionStringToConvert = common.getString("ar")
            region = Region.parseRegionCode(regionStringToConvert)
        }


        if (displays != null) {
            for (i <- 0 until displays.size) {
                val displayItem = displays.getJSONObject(i)
                if (displayItem != null && displayItem.getString("display_type") != null) {
                    val typeStr = displayItem.getString("display_type")
                    if (typeStr != null) {
                        val itemIds = MyDataParserUtils.parseItemIds(displayItem)
                        val activityType = ProductActivityType.parseDataString(typeStr)
                        for (id <- itemIds) {
                            itemActivityList.append(new ProductActivityDomain(id, activityType, null, region, 0, timestamp))
                        }
                    }
                }
            }
        }

        if (page != null) {
            val sourceType = page.getString("source_type")
            val itemIds = MyDataParserUtils.parseItemIds(page)
            val pageType = page.getString("page_id")
            val viewDuration = page.getLong("during_time")
            if (sourceType != null) {
                for (id <- itemIds) {
                    val entranceType = ProductActivityType.parseDataString(sourceType)
                    itemActivityList.append(
                        new ProductActivityDomain(id, null, entranceType, region, 0, timestamp)
                    )
                }
            }
        }

        itemActivityList
    }

}
