package dev.jens.route_analysis

import com.alibaba.fastjson.{JSON, JSONObject}
import dev.jens.utils.{MyESUtils, MyKafkaUtils, MyRedisUtils}
import io.searchbox.client.JestClient
import io.searchbox.core.Index
import java.text.SimpleDateFormat
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.{Seconds, StreamingContext}
import redis.clients.jedis.Jedis
import scala.collection.mutable.ListBuffer

object RouteAnalysis {

    val ROUTE_HASH_KEY = "route"
    val ROUTE_ANALYSIS_INDEX_PREFIX = "route_analysis"
    val NODE_SEPARATOR = " -> "

    def main(args: Array[String]): Unit = {
//        debug()
        execute(appLogic)
    }

    def appLogic(kafkaStream: InputDStream[ConsumerRecord[String, String]]): Unit = {
        kafkaStream.foreachRDD(rdd => {
            rdd.foreachPartition(itr => {
                val jedisClient = MyRedisUtils.getJedisClient()
                val jestClient = MyESUtils.getJestClient()
                itr.foreach(record => {
                    val json = JSON.parseObject(record.value())
                    eachRecord(json, jedisClient, jestClient)
                })
                jedisClient.close()
            })
        })
    }

    private def eachRecord(json: JSONObject, jedisClient: Jedis, jestClient: JestClient): Unit = {
        val uid = json.getJSONObject("common").getString("uid")
        val timestamp = json.getLong("ts")

        val last_page_string = json.getJSONObject("page").getString("last_page_id")
        val last_page = PageType.parseString(last_page_string)

        val current_page_string = json.getJSONObject("page").getString("page_id")
        val current_page = PageType.parseString(current_page_string)

        if (uid == null || current_page == null) return

        val last_route = jedisClient.hget(ROUTE_HASH_KEY, uid)

        println(s"uid: ${if (uid != null) uid}, last_page: ${if (last_page != null) last_page}, current_page: ${if (current_page != null) current_page}, last_route: ${if (last_route != null) last_route}")

        if (last_route != null) {
            val nodes = last_route.split(NODE_SEPARATOR)
            val lastPageIfContinuous = PageType.parseString(nodes.last)
            if (lastPageIfContinuous != null) {
                if (!lastPageIfContinuous.equals(last_page)) {
                    // if the route is not continuous, that means the previous route is completed
                    // hence we store it as a complete route for a user at a specific timestamp
                    uploadToElasticsearch(uid, last_route, timestamp, jestClient)
                    // we now delete the previous route so that next time we can start a new route
                    jedisClient.hdel(ROUTE_HASH_KEY, uid)
                } else if (last_page != null) {
                    // if the route is continues, we just append the current page to the previous route
                    // to make a new route
                    var new_route = ""
                    // if last_route is not null, meaning we are in the middle of a route
                    // we just append the current page to the previous route
                    new_route = last_route + NODE_SEPARATOR + current_page
                    jedisClient.hset(ROUTE_HASH_KEY, uid, new_route)
                }
            }
        } else {
            // is last route is null, we start a new route
            var new_route = ""
            if (last_page == null) {
                new_route = current_page.toString
            } else {
                new_route = last_page + NODE_SEPARATOR + current_page
            }
            jedisClient.hset(ROUTE_HASH_KEY, uid, new_route)
        }
    }

    private def uploadToElasticsearch(uid: String, routeString: String, timestamp: Long, jestClient: JestClient): Unit = {
        val route = Route(uid, routeString, timestamp)
        val date = new SimpleDateFormat("yyyy-MM-dd").format(timestamp)
        val index = new Index.Builder(route).index(ROUTE_ANALYSIS_INDEX_PREFIX + "_" + date).`type`("_doc").build()
        jestClient.execute(index)
        println(s"SUBMIT !!! uid: $uid, route: $route, timestamp: $timestamp")
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

    def debug(): Unit = {
        val jsons = new ListBuffer[String]
        jsons.append("{\"common\":{\"ar\":\"440000\",\"uid\":\"415\",\"os\":\"Android 11.0\",\"ch\":\"xiaomi\",\"md\":\"Xiaomi 10 Pro \",\"mid\":\"mid_486\",\"vc\":\"v2.1.134\",\"ba\":\"Xiaomi\"},\"page\":{\"page_id\":\"payment\",\"item\":\"19,11\",\"during_time\":13125,\"item_type\":\"sku_ids\",\"last_page_id\":\"trade\"},\"ts\":1677832304073}")
        jsons.append("{\"common\":{\"ar\":\"440000\",\"uid\":\"415\",\"os\":\"Android 11.0\",\"ch\":\"xiaomi\",\"md\":\"Xiaomi 10 Pro \",\"mid\":\"mid_486\",\"vc\":\"v2.1.134\",\"ba\":\"Xiaomi\"},\"page\":{\"page_id\":\"trade\",\"item\":\"6\",\"during_time\":14090,\"item_type\":\"sku_ids\",\"last_page_id\":\"cart\"},\"actions\":[{\"action_id\":\"trade_add_address\",\"ts\":1677832297028}],\"ts\":1677832289983}")
        jsons.append("{\"common\":{\"ar\":\"110000\",\"uid\":\"282\",\"os\":\"iOS 13.3.1\",\"ch\":\"Appstore\",\"md\":\"iPhone X\",\"mid\":\"mid_251\",\"vc\":\"v2.1.132\",\"ba\":\"iPhone\"},\"page\":{\"page_id\":\"home\",\"during_time\":18946},\"displays\":[{\"display_type\":\"activity\",\"item\":\"2\",\"item_type\":\"activity_id\",\"order\":1},{\"display_type\":\"promotion\",\"item\":\"4\",\"item_type\":\"sku_id\",\"order\":2},{\"display_type\":\"query\",\"item\":\"14\",\"item_type\":\"sku_id\",\"order\":3},{\"display_type\":\"query\",\"item\":\"7\",\"item_type\":\"sku_id\",\"order\":4},{\"display_type\":\"query\",\"item\":\"9\",\"item_type\":\"sku_id\",\"order\":5},{\"display_type\":\"query\",\"item\":\"1\",\"item_type\":\"sku_id\",\"order\":6},{\"display_type\":\"query\",\"item\":\"5\",\"item_type\":\"sku_id\",\"order\":7},{\"display_type\":\"query\",\"item\":\"2\",\"item_type\":\"sku_id\",\"order\":8},{\"display_type\":\"query\",\"item\":\"10\",\"item_type\":\"sku_id\",\"order\":9},{\"display_type\":\"query\",\"item\":\"4\",\"item_type\":\"sku_id\",\"order\":10},{\"display_type\":\"promotion\",\"item\":\"5\",\"item_type\":\"sku_id\",\"order\":11}],\"ts\":1677832235690}")
        jsons.append("{\"common\":{\"ar\":\"110000\",\"uid\":\"282\",\"os\":\"iOS 13.3.1\",\"ch\":\"Appstore\",\"md\":\"iPhone X\",\"mid\":\"mid_251\",\"vc\":\"v2.1.132\",\"ba\":\"iPhone\"},\"page\":{\"page_id\":\"search\",\"during_time\":7082,\"last_page_id\":\"home\"},\"ts\":1677832254636}")
        jsons.append("{\"common\":{\"ar\":\"110000\",\"uid\":\"282\",\"os\":\"iOS 13.3.1\",\"ch\":\"Appstore\",\"md\":\"iPhone X\",\"mid\":\"mid_251\",\"vc\":\"v2.1.132\",\"ba\":\"iPhone\"},\"page\":{\"page_id\":\"good_detail\",\"item\":\"3\",\"during_time\":13975,\"item_type\":\"sku_id\",\"last_page_id\":\"good_list\",\"source_type\":\"promotion\"},\"displays\":[{\"display_type\":\"query\",\"item\":\"12\",\"item_type\":\"sku_id\",\"order\":1},{\"display_type\":\"query\",\"item\":\"16\",\"item_type\":\"sku_id\",\"order\":2},{\"display_type\":\"query\",\"item\":\"17\",\"item_type\":\"sku_id\",\"order\":3},{\"display_type\":\"query\",\"item\":\"19\",\"item_type\":\"sku_id\",\"order\":4},{\"display_type\":\"recommend\",\"item\":\"9\",\"item_type\":\"sku_id\",\"order\":5},{\"display_type\":\"promotion\",\"item\":\"16\",\"item_type\":\"sku_id\",\"order\":6},{\"display_type\":\"query\",\"item\":\"11\",\"item_type\":\"sku_id\",\"order\":7},{\"display_type\":\"query\",\"item\":\"3\",\"item_type\":\"sku_id\",\"order\":8},{\"display_type\":\"query\",\"item\":\"4\",\"item_type\":\"sku_id\",\"order\":9}],\"actions\":[{\"item\":\"3\",\"action_id\":\"favor_add\",\"item_type\":\"sku_id\",\"ts\":1677832283790}],\"ts\":1677832276803}")
        jsons.append("{\"common\":{\"ar\":\"110000\",\"uid\":\"282\",\"os\":\"iOS 13.3.1\",\"ch\":\"Appstore\",\"md\":\"iPhone X\",\"mid\":\"mid_251\",\"vc\":\"v2.1.132\",\"ba\":\"iPhone\"},\"page\":{\"page_id\":\"good_list\",\"item\":\"小米手机\",\"during_time\":15085,\"item_type\":\"keyword\",\"last_page_id\":\"search\"},\"displays\":[{\"display_type\":\"query\",\"item\":\"3\",\"item_type\":\"sku_id\",\"order\":1},{\"display_type\":\"query\",\"item\":\"2\",\"item_type\":\"sku_id\",\"order\":2},{\"display_type\":\"query\",\"item\":\"3\",\"item_type\":\"sku_id\",\"order\":3},{\"display_type\":\"query\",\"item\":\"7\",\"item_type\":\"sku_id\",\"order\":4},{\"display_type\":\"recommend\",\"item\":\"19\",\"item_type\":\"sku_id\",\"order\":5},{\"display_type\":\"promotion\",\"item\":\"8\",\"item_type\":\"sku_id\",\"order\":6},{\"display_type\":\"promotion\",\"item\":\"19\",\"item_type\":\"sku_id\",\"order\":7},{\"display_type\":\"promotion\",\"item\":\"16\",\"item_type\":\"sku_id\",\"order\":8}],\"ts\":1677832261718}")
        jsons.append("{\"common\":{\"ar\":\"500000\",\"uid\":\"340\",\"os\":\"Android 11.0\",\"ch\":\"xiaomi\",\"md\":\"Honor 20s\",\"mid\":\"mid_334\",\"vc\":\"v2.1.111\",\"ba\":\"Honor\"},\"page\":{\"page_id\":\"orders_unpaid\",\"during_time\":2593,\"last_page_id\":\"mine\"},\"ts\":1677832333062}")
        jsons.append("{\"common\":{\"ar\":\"310000\",\"uid\":\"34\",\"os\":\"iOS 13.2.3\",\"ch\":\"Appstore\",\"md\":\"iPhone Xs Max\",\"mid\":\"mid_415\",\"vc\":\"v2.1.134\",\"ba\":\"iPhone\"},\"page\":{\"page_id\":\"payment\",\"item\":\"16,18,6\",\"during_time\":7236,\"item_type\":\"sku_ids\",\"last_page_id\":\"trade\"},\"ts\":1677832373651}")
        jsons.append("{\"common\":{\"ar\":\"310000\",\"uid\":\"34\",\"os\":\"iOS 13.2.3\",\"ch\":\"Appstore\",\"md\":\"iPhone Xs Max\",\"mid\":\"mid_415\",\"vc\":\"v2.1.134\",\"ba\":\"iPhone\"},\"page\":{\"page_id\":\"cart\",\"during_time\":1100,\"last_page_id\":\"good_detail\"},\"ts\":1677832354751}")
        jsons.append("{\"common\":{\"ar\":\"310000\",\"uid\":\"34\",\"os\":\"iOS 13.2.3\",\"ch\":\"Appstore\",\"md\":\"iPhone Xs Max\",\"mid\":\"mid_415\",\"vc\":\"v2.1.134\",\"ba\":\"iPhone\"},\"page\":{\"page_id\":\"trade\",\"item\":\"11\",\"during_time\":17800,\"item_type\":\"sku_ids\",\"last_page_id\":\"cart\"},\"ts\":1677832355851}")
        val jedisClient = MyRedisUtils.getJedisClient()
        val jestClient = MyESUtils.getJestClient()
        jsons.map(JSON.parseObject).foreach(eachRecord(_, jedisClient, jestClient))
        jedisClient.close()
        jestClient.close()
    }

}
