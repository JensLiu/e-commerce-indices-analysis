package dev.jens.product;

import dev.jens.Region;
import dev.jens.entity.ProductActivity;

public class ProductActivityDomain {
    private String productId;
    private ProductActivityType systemPageType;
    private ProductActivityType userEntranceType;
    private Region region;
    private Long viewDuration;

    private Long timestamp;

    @Override
    public String toString() {
        return "ProductActivityDomain{" +
                "productId='" + productId + '\'' +
                ", systemPageType=" + systemPageType +
                ", userEntranceType=" + userEntranceType +
                ", region=" + region +
                ", viewDuration=" + viewDuration +
                ", timestamp=" + timestamp +
                '}';
    }

    public ProductActivityDomain(String productId,
                                 ProductActivityType systemPageType,
                                 ProductActivityType userEntranceType,
                                 Region region,
                                 Long viewDuration,
                                 Long timestamp) {
        this.productId = productId;
        this.systemPageType = systemPageType;
        this.userEntranceType = userEntranceType;
        this.region = region;
        this.viewDuration = viewDuration;
        this.timestamp = timestamp;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public ProductActivityType getSystemPageType() {
        return systemPageType;
    }

    public void setSystemPageType(ProductActivityType systemPageType) {
        this.systemPageType = systemPageType;
    }

    public ProductActivityType getUserEntranceType() {
        return userEntranceType;
    }

    public void setUserEntranceType(ProductActivityType userEntranceType) {
        this.userEntranceType = userEntranceType;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getViewDuration() {
        return viewDuration;
    }

    public void setViewDuration(Long viewDuration) {
        this.viewDuration = viewDuration;
    }
}
