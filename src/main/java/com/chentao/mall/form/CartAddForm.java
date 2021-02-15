package com.chentao.mall.form;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 添加商品时的表单对象
 */
@Data
public class CartAddForm {

    // @NotBlank 用于String判断空格
    // @NotEmpty 用于集合
    // @NotNull
    @NotNull
    private Integer productId;

    private Boolean selected = true;
}
