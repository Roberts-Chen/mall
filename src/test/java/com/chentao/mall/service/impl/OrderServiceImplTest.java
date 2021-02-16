package com.chentao.mall.service.impl;

import com.chentao.mall.MallApplicationTests;
import com.chentao.mall.enums.ResponseEnum;
import com.chentao.mall.form.CartAddForm;
import com.chentao.mall.service.ICartService;
import com.chentao.mall.service.IOrderService;
import com.chentao.mall.vo.CartVo;
import com.chentao.mall.vo.OrderVo;
import com.chentao.mall.vo.ResponseVo;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
class OrderServiceImplTest extends MallApplicationTests {

    private Integer uid = 1;

    private Integer shippingId = 1;

    private Integer productId = 7;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Autowired
    private IOrderService orderService;

    @Autowired
    private ICartService cartService;

    @Test
    void create() {
        create_order();
    }

    @BeforeEach
    public void before() {
        CartAddForm form = new CartAddForm();
        form.setProductId(productId);
        form.setSelected(true);
        ResponseVo<CartVo> responseVo = cartService.add(uid, form);
        Assertions.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    private ResponseVo<OrderVo> create_order() {
        ResponseVo<OrderVo> responseVo = orderService.create(uid, shippingId);
        log.info("create：{}", gson.toJson(responseVo));
        Assertions.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
        return responseVo;
    }

    @Test
    void list() {
        ResponseVo<OrderVo> order = create_order();
        ResponseVo<PageInfo> responseVo = orderService.list(uid, 1, 2);
        log.info("list：{}", gson.toJson(responseVo));
        Assertions.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    @Test
    void detail() {
        ResponseVo<OrderVo> order = create_order();
        ResponseVo<OrderVo> responseVo = orderService.detail(uid, order.getData().getOrderNo());
        log.info("detail: {}", gson.toJson(responseVo));
        Assertions.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    @Test
    void cancel() {
        ResponseVo<OrderVo> order = create_order();
        ResponseVo responseVo = orderService.cancel(uid, order.getData().getOrderNo());
        log.info("detail: {}", gson.toJson(responseVo));
        Assertions.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }
}