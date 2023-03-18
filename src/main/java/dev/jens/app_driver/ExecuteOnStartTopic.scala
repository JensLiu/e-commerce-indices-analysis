package dev.jens.app_driver

import dev.jens.daily_active_user_analysis.DailyActiveUserAnalysis
import dev.jens.utils.MyKafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

object ExecuteOnStartTopic {
    def main(args: Array[String]): Unit = {
        val startAnalysisContext: StreamingContext = new StreamingContext(
            "local[3]",
            "start_analysis",
            Seconds(5)
        )
        val startAnalysisStream = MyKafkaUtils.createKafkaStream(
            streamingContext = startAnalysisContext,
            topic = "start",
            groupId = "start-analysis-consumer-group-01",
            offset = Map()
        )
        DailyActiveUserAnalysis.appLogic(startAnalysisStream)
        startAnalysisContext.start()
        startAnalysisContext.awaitTermination()
    }
}
