package com.chentao.mall.service.impl;

import com.chentao.mall.MallApplicationTests;
import com.chentao.mall.enums.ResponseEnum;
import com.chentao.mall.form.ShippingForm;
import com.chentao.mall.service.IShippingService;
import com.chentao.mall.vo.ResponseVo;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
@Slf4j
class ShippingServiceImplTest extends MallApplicationTests {

    @Autowired
    IShippingService shippingService;

    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private Integer uid = 1;
    private Integer shippingId;

    private ShippingForm form;


//    @Test
    @BeforeEach
    void add() {
        ShippingForm form = new ShippingForm();
        form.setReceiverName("陈涛");
        form.setReceiverPhone("110592");
        form.setReceiverMobile("13428835966");
        form.setReceiverProvince("广东");
        form.setReceiverCity("广州");
        form.setReceiverAddress("中国");
        form.setReceiverDistrict("天河区");
        form.setReceiverZip("123456");
        this.form = form;
        ResponseVo<Map<String, Integer>> responseVo = shippingService.add(1, form);
        log.info("add: {}", gson.toJson(responseVo));
        this.shippingId = responseVo.getData().get("shippingId");
        Assertions.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    @Test
    void addBatch() {
        String[] cities = {"广州", "惠州", "韶关", "清远", "雷州", "梅州", "潮州"};
        ShippingForm form = new ShippingForm();
        form.setReceiverName("陈涛");
        form.setReceiverPhone("110592");
        form.setReceiverMobile("13428835966");
        form.setReceiverProvince("广东");
        form.setReceiverAddress("中国");
        form.setReceiverDistrict("天河区");
        form.setReceiverZip("123456");
        for (int i = 0; i < 100; i++) {
            form.setReceiverCity(cities[i % 7]);
            ResponseVo<Map<String, Integer>> responseVo = shippingService.add(1, form);
        }
    }

    @Test
    void delete() {
        ResponseVo responseVo = shippingService.delete(uid, shippingId);
        log.info("delete: {}", gson.toJson(responseVo));
        Assertions.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    @Test
    void update() {
        form.setReceiverCity("佛山");
        ResponseVo responseVo = shippingService.update(uid, 1, form);
        log.info("update: {}", gson.toJson(responseVo));
        Assertions.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    @Test
    void list() {
        ResponseVo<PageInfo> responseVo = shippingService.list(uid, 1, 5);
        log.info("list: {}", gson.toJson(responseVo));
        Assertions.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }
}