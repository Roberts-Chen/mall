package com.chentao.mall.form;

import lombok.Data;

/**
 * 购物车更新的表单对象
 */
@Data
public class CartUpdateForm {

    private Integer quantity;

    private Boolean selected;
}
