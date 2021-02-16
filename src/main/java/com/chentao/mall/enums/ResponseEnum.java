package com.chentao.mall.enums;

import lombok.Getter;

@Getter
public enum ResponseEnum {
    ERROR(-1, "服务端错误"),

    SUCCESS(0, "成功"),

    PASSWORD_ERROR(1, "密码错误"),

    USERNAME_EXIST(2, "用户名已存在"),

    NEED_LOGIN(3, "用户未登录，请先登录"),

    PARAM_ERROR(4, "参数错误"),

    EMAIL_EXIST(5, "邮箱已存在"),

    USERNAME_OR_PASSWORD_ERROR(6, "用户名或密码错误"),

    PRODUCT_OF_SALE_OR_DELETE(7,"商品下架或删除"),

    PRODUCT_NOT_EXIST(8, "商品不存在"),

    STOCK_ERROR(9, "库存不足"),

    CART_PRODUCT_NOT_EXIST(10, "购物车中无此商品"),

    DELETE_SHIPPING_FAIL(11, "删除收货地址失败"),

    SHIPPING_NOT_EXIST(12, "收货地址不存在"),

    CART_SELECTED_IS_EMPTY(13, "请选择要下单的商品"),

    ORDER_NOT_EXIST(14, "订单不存在"),

    ORDER_STATUS_ERROR(15, "订单状态错误"),
    ;


    private Integer code;
    private String msg;

    ResponseEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
