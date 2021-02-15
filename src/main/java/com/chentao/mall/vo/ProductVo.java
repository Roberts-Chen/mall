package com.chentao.mall.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVo {
    private Integer id;

    private Integer categoryId;

    private String name;

    private String subtitle;

    private String mainImages;

    private BigDecimal price;

    private Integer status;
}
