package dev.jens.product_analysis

import com.alibaba.fastjson.{JSON, JSONArray, JSONObject}
import dev.jens.enums.{MyRegion, ProductActivityType}
import scala.collection.mutable.ListBuffer

object DataParser {
    def parseJsonToDomain(jsonString: String): Iterable[ProductActivityDomain] = {
        val itemActivityList = new ListBuffer[ProductActivityDomain]

        val jsonObject = JSON.parseObject(jsonString)

        val common: JSONObject = jsonObject.getJSONObject("common")
        val displays: JSONArray = jsonObject.getJSONArray("displays")
        val page: JSONObject = jsonObject.getJSONObject("page")
        val timestamp: Long = jsonObject.getLong("ts")
        var region: MyRegion = null

        if (common != null) {
            val regionStringToConvert = common.getString("ar")
            region = MyRegion.parseRegionCode(regionStringToConvert)
        }


        if (displays != null) {
            for (i <- 0 until displays.size) {
                val displayItem = displays.getJSONObject(i)
                if (displayItem != null && displayItem.getString("display_type") != null) {
                    val typeStr = displayItem.getString("display_type")
                    if (typeStr != null) {
                        val itemIds = parseItemIds(displayItem)
                        val activityType = ProductActivityType.parseActivityType(typeStr)
                        for (id <- itemIds) {
                            itemActivityList.append(new ProductActivityDomain(id, activityType, null, region, timestamp))
                        }
                    }
                }
            }
        }

        if (page != null) {
            val sourceType = page.getString("source_type")
            val itemIds = parseItemIds(page)
            if (sourceType != null) {
                for (id <- itemIds) {
                    val entranceType = ProductActivityType.parseActivityType(sourceType)
                    itemActivityList.append(
                        new ProductActivityDomain(id, null, entranceType, region, timestamp)
                    )
                }
            }
        }

        itemActivityList
    }

    def parseItemIds(json: JSONObject): List[String] = {
        val itemId = json.getString("item")
        val itemType = json.getString("item_type")
        val list = new ListBuffer[String]
        itemType match {
            case "sku_id" => list.append(itemId)
            case "sku_ids" => list.appendAll(itemId.split(","))
            case _ =>
        }
        list.toList
    }
}
