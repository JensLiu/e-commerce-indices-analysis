package dev.jens.utils

import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.Properties

object MyPropertyUtils {

    def main(args: Array[String]): Unit = {
        val properties = load("config.properties")
        println(properties.getProperty("redis.host"))
    }

    def load(propFileName: String): Properties = {
        val properties = new Properties()
        properties.load(new InputStreamReader(
            Thread.currentThread().getContextClassLoader.getResourceAsStream(propFileName),
            StandardCharsets.UTF_8
        ))
        properties
    }
}
