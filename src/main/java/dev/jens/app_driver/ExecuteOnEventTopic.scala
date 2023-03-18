package dev.jens.app_driver

import dev.jens.product_analysis.ProductAnalysis
import dev.jens.user_route_analysis.RouteAnalysis
import dev.jens.utils.MyKafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

object ExecuteOnEventTopic {
    def main(args: Array[String]): Unit = {
        val eventAnalysisContext: StreamingContext = new StreamingContext(
            "local[3]",
            "event_analysis",
            Seconds(5)
        )
        val eventAnalysisStream = MyKafkaUtils.createKafkaStream(
            streamingContext = eventAnalysisContext,
            topic = "event",
            groupId = "event-analysis-consumer-group-01",
            offset = Map()
        )
        ProductAnalysis.appLogic(eventAnalysisStream)
        RouteAnalysis.appLogic(eventAnalysisStream)
        eventAnalysisContext.start()
        eventAnalysisContext.awaitTermination()
    }
}
