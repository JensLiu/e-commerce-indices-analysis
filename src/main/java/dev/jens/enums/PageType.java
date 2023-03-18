package dev.jens.user_route_analysis;

public enum PageType {

    HOME("首页"),
    CATEGORY("分类页"),
    DISCOVERY("发现页"),
    TOP_BOARD("热门排行"),
    FAVOURITE("收藏页"),
    SEARCH("搜索页"),
    COMMODITY_LIST("商品列表页"),
    COMMODITY_DETAIL("商品详情"),
    COMMODITY_SPECIFICATION("商品规格"),
    COMMENT("评价"),
    COMMENT_FINISHED("评价完成"),
    COMMENT_LIST("评价列表"),
    CART("购物车"),
    TRADE("下单结算"),
    PAYMENT("支付页面"),
    PAYMENT_SUCCESS("支付完成"),
    ORDERS_ALL("全部订单"),
    ORDERS_UNPAID("订单待支付"),
    ORDERS_UNDELIVERED("订单待发货"),
    ORDERS_UNRECEPTED("订单待收货"),
    ORDERS_TO_COMMENT("订单待评价"),
    MINE_PAGE("我的"),
    ACTIVITY("活动"),
    LOGIN("登录"),
    REGISTER("注册");

    private String code;

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
