package dev.jens.enums;

public enum MyRegion {
    CHN_BEIJING("CN-BJ"),
    CHN_SHANGHAI("CN-SH"),
    CHN_HEILONGJIANG("CN-HL"),
    CHN_SHANDONG("CN-SD"),
    CHN_HUBEI("CN-HB"),
    CHN_GUANGDONG("CN-GD"),
    CHN_CHONGQING("CN-CQ"),
    CHN_YUNNAN("CN-YN");
    private String code;
    MyRegion(String s) {
        this.code = s;
    }

    public String getCode() {
        return this.code;
    }
    public static MyRegion parseRegionCode(String regionCode) {
        switch (regionCode) {
            case "110000": return CHN_BEIJING;
            case "310000": return CHN_SHANGHAI;
            case "230000": return CHN_HEILONGJIANG;
            case "370000": return CHN_SHANDONG;
            case "420000": return CHN_HUBEI;
            case "440000": return CHN_GUANGDONG;
            case "500000": return CHN_CHONGQING;
            case "530000": return CHN_YUNNAN;
        }
        return null;
    }
}
