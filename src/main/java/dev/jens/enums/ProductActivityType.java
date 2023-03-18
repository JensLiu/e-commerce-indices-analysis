package dev.jens.enums;

public enum ProductActivityType {
    PRODUCT_PROMOTION,
    SALES_PROMOTION,
    SEARCHED,
    RECOMMENDATION;

    public static ProductActivityType parseActivityType(String string) {
        switch (string) {
            case "recommend": return ProductActivityType.RECOMMENDATION;
            case "promotion": return ProductActivityType.PRODUCT_PROMOTION;
            case "activity": return ProductActivityType.SALES_PROMOTION;
            case "query": return ProductActivityType.SEARCHED;
        }
        return null;
    }

}