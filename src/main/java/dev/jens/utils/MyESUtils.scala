package dev.jens.utils

import io.searchbox.client.{JestClient, JestClientFactory}
import io.searchbox.client.config.HttpClientConfig
import io.searchbox.core.{Bulk, Index}

object MyESUtils {
    private var jestClientFactory: JestClientFactory = null

    def getJestClient(): JestClient = {
        if (jestClientFactory == null) {
            buildJestClientFactory()
        }
        jestClientFactory.getObject
    }

    private def buildJestClientFactory(): Unit = {
        jestClientFactory = new JestClientFactory()
        val properties = MyPropertyUtils.load("config.properties")
        val host = properties.getProperty("es.host")
        jestClientFactory.setHttpClientConfig(
            new HttpClientConfig.Builder(
                s"http://$host:9200"
            )
                    .multiThreaded(true)
                    .maxTotalConnection(20)
                    .connTimeout(10000)
                    .readTimeout(2000)
                    .build())
    }

    def bulkInsertWithIds(records: List[(String, Any)], indexName: String): Unit = {
        if (records != null && records.size != 0) {
            val client = getJestClient()
            val bulkBuilder = new Bulk.Builder()
            for ((id, record) <- records) {
                println(record)
                val index = new Index.Builder(record).index(indexName).id(id).`type`("_doc").build()
                bulkBuilder.addAction(index)
            }
            val bulk = bulkBuilder.build()
            val result = client.execute(bulk)
            println(result.getItems.size() + " inserted to " + indexName)
            client.close()
        }
    }

    def bulkInsert(records: List[Any], indexName: String): Unit = {
        if (records != null && records.size != 0) {
            val client = getJestClient()
            val bulkBuilder = new Bulk.Builder()
            for ((id, record) <- records) {
                println(record)
                val index = new Index.Builder(record).index(indexName).`type`("_doc").build()
                bulkBuilder.addAction(index)
            }
            val bulk = bulkBuilder.build()
            val result = client.execute(bulk)
            println(result.getItems.size() + " inserted to " + indexName)
            client.close()
        }
    }

}
