package dev.jens.product;

public class SystemRecommendationAnalysisDto {

    String action;

    public SystemRecommendationAnalysisDto(String type) {
        this.action = type;
    }

    public static SystemRecommendationAnalysisDto fromDomain(ProductActivityDomain domain) {
        if (domain.getSystemPageType() == ProductActivityType.RECOMMENDATION) {
            System.out.println("system_recommendation");
            return new SystemRecommendationAnalysisDto("算法推荐");
        } else if (domain.getUserEntranceType() == ProductActivityType.RECOMMENDATION) {
            System.out.println("entered_from_recommendation");
            return new SystemRecommendationAnalysisDto("实际吸引人数");
        }
//        System.out.println("not suitable");
        return null;
    }

}
