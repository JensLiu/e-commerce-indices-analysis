package dev.jens.executor

import dev.jens.user.DailyActiveUserAnalysis
import dev.jens.utils.MyKafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

object ExecuteOnStartTopic {
    def main(args: Array[String]): Unit = {
        val startAnalysisContext: StreamingContext = new StreamingContext("local[3]", "start_analysis", Seconds(3))
        val startAnalysisStream = MyKafkaUtils.createKafkaStream(
            streamingContext = startAnalysisContext,
            topic = "start",
            groupId = "start-analysis-consumer-group",
            offset = Map()
        )
        DailyActiveUserAnalysis.appLogic(startAnalysisStream)
        startAnalysisContext.start()
        startAnalysisContext.awaitTermination()
    }
}
