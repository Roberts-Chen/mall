package com.chentao.mall.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartVo {
    private List<CartProductVo> cartProductVoList;

    // 全选
    private boolean selectAll;

    // 购物车中所有商品的总价
    private BigDecimal cartTotalPrice;

    // 购物车中所有商品的总数
    private Integer cartTotalQuantity;

}
