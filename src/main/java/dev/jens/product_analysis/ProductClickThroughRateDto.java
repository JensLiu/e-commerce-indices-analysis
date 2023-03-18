package dev.jens.product;

public class ProductActivityDto {

    private String sku_id = "unspecified";
    private int system_search = 0;
    private int system_recommendation = 0;
    private int system_product_promotion = 0;
    private int entered_from_product_promotion = 0;
    private int system_sales_promotion = 0;

    private int entered_from_recommendation = 0;
    private int entered_from_search = 0;
    private int entered_from_sales_promotion = 0;
    private long timestamp = 0;
    private String geo_code = "";

    @Override
    public String toString() {
        return "ProductActivityDto{" +
                "sku_id='" + sku_id + '\'' +
                ", system_search=" + system_search +
                ", system_recommendation=" + system_recommendation +
                ", system_product_promotion=" + system_product_promotion +
                ", entered_from_product_promotion=" + entered_from_product_promotion +
                ", system_sales_promotion=" + system_sales_promotion +
                ", entered_from_recommendation=" + entered_from_recommendation +
                ", entered_from_search=" + entered_from_search +
                ", entered_from_sales_promotion=" + entered_from_sales_promotion +
                ", timestamp=" + timestamp +
                ", geo_code='" + geo_code + '\'' +
                '}';
    }

    public ProductActivityDto(ProductActivityDomain domain) {
        System.out.println("domain: " + domain);
        this.sku_id = domain.getProductId();
        this.timestamp = domain.getTimestamp();
        this.geo_code = domain.getRegion().getCode();
        if (domain.getSystemPageType() != null) {
            switch (domain.getSystemPageType()) {
                case SEARCHED:
                    this.system_search = 1;
                    break;
                case RECOMMENDATION:
                    this.system_recommendation = 1;
                    break;
                case PRODUCT_PROMOTION:
                    this.system_product_promotion = 1;
                    break;
                case SALES_PROMOTION:
                    this.system_sales_promotion = 1;
                    break;
            }
        }
        if (domain.getUserEntranceType() != null) {
            switch (domain.getUserEntranceType()) {
                case SEARCHED:
                    this.entered_from_search = 1;
                    break;
                case RECOMMENDATION:
                    this.entered_from_recommendation = 1;
                    break;
                case PRODUCT_PROMOTION:
                    this.entered_from_product_promotion = 1;
                    break;
                case SALES_PROMOTION:
                    this.entered_from_sales_promotion = 1;
                    break;
            }
        }
    }

}
