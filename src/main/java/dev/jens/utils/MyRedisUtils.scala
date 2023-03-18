package dev.jens.utils

import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}

object MyRedisUtils {
    private var jedisPool: JedisPool = null;
    def build(): Unit = {
        val properties = MyPropertyUtils.load("config.properties")
        val host = properties.get("redis.host").asInstanceOf[String]
        val port = properties.get("redis.port").asInstanceOf[String]
        println(s"host: ${host}, port: ${port}")

        val jedisPoolConfig = new JedisPoolConfig
        jedisPoolConfig.setMaxTotal(100)    // max number of connections
        jedisPoolConfig.setMaxIdle(20)
        jedisPoolConfig.setMinIdle(29)
        jedisPoolConfig.setBlockWhenExhausted(true)
        jedisPoolConfig.setMaxWaitMillis(5000)
        jedisPoolConfig.setTestOnBorrow(true)   // test on each connection establishment

        jedisPool = new JedisPool(jedisPoolConfig, host, port.toInt)
    }

    def getJedisClient(): Jedis = {
        if (jedisPool == null) {
            build()
        }
        jedisPool.getResource
    }

    def main(args: Array[String]): Unit = {
        val jedis = getJedisClient()
        println(jedis.ping())
    }

}
