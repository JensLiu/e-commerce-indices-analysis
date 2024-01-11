package dev.jens.product_analysis;

import dev.jens.enums.ProductActivityType;

public class SystemRecommendationAnalysisDto {

    String action;

    public SystemRecommendationAnalysisDto(String type) {
        this.action = type;
    }

    public static SystemRecommendationAnalysisDto fromDomain(ProductActivityDomain domain) {
        if (domain.getSystemActivityType() == ProductActivityType.RECOMMENDATION) {
            System.out.println("system_recommendation");
            // system recommendation displayed
            return new SystemRecommendationAnalysisDto("recommended");
        } else if (domain.getUserEntranceFromType() == ProductActivityType.RECOMMENDATION) {
            System.out.println("entered_from_recommendation");
            // user clicked through recommendation
            return new SystemRecommendationAnalysisDto("entered");
        }
        return null;
    }

}
