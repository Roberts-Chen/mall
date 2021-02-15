package com.chentao.mall.form;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 购物车更新的表单对象
 */
@Data
public class CartUpdateForm {

    @NotNull
    private Integer quantity;

    @NotNull
    private Boolean selected;
}
