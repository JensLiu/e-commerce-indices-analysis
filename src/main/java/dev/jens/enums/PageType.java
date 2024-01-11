package dev.jens.enums;

public enum PageType {

    HOME("home"),
    CATEGORY("category"),
    DISCOVERY("discovery"),
    TOP_BOARD("top_board"),
    FAVOURITE("favourite"),
    SEARCH("search"),
    COMMODITY_LIST("commodity_list"),
    COMMODITY_DETAIL("commodity_detail"),
    COMMODITY_SPECIFICATION("commodity_specification"),
    COMMENT("comment"),
    COMMENT_FINISHED("comment_finished"),
    COMMENT_LIST("comment_list"),
    CART("cart"),
    TRADE("trade"),
    PAYMENT("payment"),
    PAYMENT_SUCCESS("payment_success"),
    ORDERS_ALL("orders_all"),
    ORDERS_UNPAID("orders_unpaid"),
    ORDERS_UNDELIVERED("orders_undelivered"),
    ORDERS_UNRECEPTED("orders_unrecepted"),
    ORDERS_TO_COMMENT("orders_to_comment"),
    MINE_PAGE("mine"),
    ACTIVITY("activity"),
    LOGIN("login"),
    REGISTER("register");

    private final String code;

    PageType(String string) {
        this.code = string;
    }

    @Override
    public String toString() {
        return this.code;
    }

    public String getCode() {
        return this.code;
    }

    public static PageType parseString(String string) {
        if (string == null)
            return null;
        switch (string) {
            case "home":
            case "首页":
                return HOME;
            case "category":
            case "分类页":
                return CATEGORY;
            case "discovery":
            case "发现页":
                return DISCOVERY;
            case "top_n":
            case "热门排行":
                return TOP_BOARD;
            case "favor":
            case "收藏页":
                return FAVOURITE;
            case "search":
            case "搜索页":
                return SEARCH;
            case "good_list":
            case "商品列表页":
                return COMMODITY_LIST;
            case "good_detail":
            case "商品详情":
                return COMMODITY_DETAIL;
            case "good_spec":
            case "商品规格":
                return COMMODITY_SPECIFICATION;
            case "comment":
            case "评价":
                return COMMENT;
            case "comment_done":
            case "评价完成":
                return COMMENT_FINISHED;
            case "comment_list":
            case "评价列表":
                return COMMENT_LIST;
            case "cart":
            case "购物车":
                return CART;
            case "trade":
            case "下单结算":
                return TRADE;
            case "payment":
            case "支付页面":
                return PAYMENT;
            case "payment_done":
            case "支付完成":
                return PAYMENT_SUCCESS;
            case "orders_all":
            case "全部订单":
                return ORDERS_ALL;
            case "orders_unpaid":
            case "订单待支付":
                return ORDERS_UNPAID;
            case "orders_undelivered":
            case "订单待发货":
                return ORDERS_UNDELIVERED;
            case "orders_unreceipted":
            case "订单待收货":
                return ORDERS_UNRECEPTED;
            case "orders_wait_comment":
            case "订单待评价":
                return ORDERS_TO_COMMENT;
            case "mine":
            case "我的":
                return MINE_PAGE;
            case "activity":
            case "活动":
                return ACTIVITY;
            case "login":
            case "登录":
                return LOGIN;
            case "register":
            case "注册":
                return REGISTER;
            default:
                return null;
        }
    }

}
