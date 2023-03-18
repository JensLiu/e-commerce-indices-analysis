package dev.jens.product_analysis;

import dev.jens.enums.MyRegion;
import dev.jens.enums.ProductActivityType;

public class ProductActivityDomain {
    private String skuId;
    private ProductActivityType systemActivityType;
    private ProductActivityType userEntranceFromType;
    private MyRegion region;

    private Long timestamp;

    @Override
    public String toString() {
        return "ProductActivityDomain{" +
                "productId='" + skuId + '\'' +
                ", systemPageType=" + systemActivityType +
                ", userEntranceType=" + userEntranceFromType +
                ", region=" + region +
                ", timestamp=" + timestamp +
                '}';
    }

    public ProductActivityDomain(String skuId,
                                 ProductActivityType systemPageType,
                                 ProductActivityType userEntranceType,
                                 MyRegion region,
                                 Long timestamp) {
        this.skuId = skuId;
        this.systemActivityType = systemPageType;
        this.userEntranceFromType = userEntranceType;
        this.region = region;
        this.timestamp = timestamp;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public ProductActivityType getSystemActivityType() {
        return systemActivityType;
    }

    public void setSystemActivityType(ProductActivityType systemActivityType) {
        this.systemActivityType = systemActivityType;
    }

    public ProductActivityType getUserEntranceFromType() {
        return userEntranceFromType;
    }

    public void setUserEntranceFromType(ProductActivityType userEntranceFromType) {
        this.userEntranceFromType = userEntranceFromType;
    }

    public MyRegion getRegion() {
        return region;
    }

    public void setRegion(MyRegion region) {
        this.region = region;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
