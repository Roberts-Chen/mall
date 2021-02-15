package com.chentao.mall.service.impl;

import com.chentao.mall.MallApplicationTests;
import com.chentao.mall.enums.ResponseEnum;
import com.chentao.mall.service.IOrderService;
import com.chentao.mall.vo.OrderVo;
import com.chentao.mall.vo.ResponseVo;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
class OrderServiceImplTest extends MallApplicationTests {

    private Integer uid = 1;

    private Integer shippingId = 1;

    @Autowired
    private IOrderService orderService;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    void create() {
        ResponseVo<OrderVo> responseVo = orderService.create(uid, shippingId);
        log.info("create：{}", gson.toJson(responseVo));
        Assertions.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    @Test
    void list() {
        ResponseVo<PageInfo> responseVo = orderService.list(uid, 1, 2);
        log.info("list：{}", gson.toJson(responseVo));
        Assertions.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    @Test
    void detail() {
        ResponseVo<OrderVo> responseVo = orderService.detail(uid, 1613384769422L);
        log.info("detail: {}", gson.toJson(responseVo));
        Assertions.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    @Test
    void cancel() {
        ResponseVo responseVo = orderService.cancel(uid, 1613384769422L);
        log.info("detail: {}", gson.toJson(responseVo));
        Assertions.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }
}