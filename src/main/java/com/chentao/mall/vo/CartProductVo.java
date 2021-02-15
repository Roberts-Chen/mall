package com.chentao.mall.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartProductVo {
    /**
     * 商品Id
     */
    private Integer productId;

    /**
     * 商品数量
     */
    private Integer quantity;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品子标题
     */
    private String productSubtitle;

    /**
     * 商品主图
     */
    private String productMainImages;

    /**
     * 商品单价
     */
    private BigDecimal productPrice;

    /**
     * 商品状态
     */
    private Integer productStatus;

    /**
     * 商品总价
     */
    private BigDecimal productTotalPrice;

    /**
     * 商品库存
     */
    private Integer productStock;

    /**
     * 是否选中该商品
     */
    private Boolean productSelected;

    public CartProductVo(Integer productId, Integer quantity, String productName, String productSubtitle, String productMainImages, BigDecimal productPrice, Integer productStatus, BigDecimal productTotalPrice, Integer productStock, Boolean productSelected) {
        this.productId = productId;
        this.quantity = quantity;
        this.productName = productName;
        this.productSubtitle = productSubtitle;
        this.productMainImages = productMainImages;
        this.productPrice = productPrice;
        this.productStatus = productStatus;
        this.productTotalPrice = productTotalPrice;
        this.productStock = productStock;
        this.productSelected = productSelected;
    }

    public CartProductVo() {
    }
}
