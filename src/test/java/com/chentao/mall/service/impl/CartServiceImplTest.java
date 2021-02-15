package com.chentao.mall.service.impl;

import com.chentao.mall.MallApplicationTests;
import com.chentao.mall.enums.ResponseEnum;
import com.chentao.mall.form.CartAddForm;
import com.chentao.mall.form.CartUpdateForm;
import com.chentao.mall.service.ICartService;
import com.chentao.mall.vo.CartVo;
import com.chentao.mall.vo.ResponseVo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

@Slf4j
class CartServiceImplTest extends MallApplicationTests {
    @Autowired
    ICartService cartService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private Integer uid = 1;
    private Integer productId = 7;

//    @BeforeEach
    @Test
    void add() {
        CartAddForm form = new CartAddForm();
        for (int i = 1; i <= 20; i++) {
//            form.setProductId(productId);
            form.setProductId(i);
            form.setSelected(true);
            ResponseVo<CartVo> responseVo = cartService.add(uid, form);
        }
//        log.info("add: {}", gson.toJson(responseVo));
//        Assertions.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    @Test
    void list() {
        ResponseVo<CartVo> responseVo = cartService.list(uid);
        log.info("list: {}", gson.toJson(responseVo));
        Assertions.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    @Test
    void update() {
        CartUpdateForm form = new CartUpdateForm();
        form.setQuantity(99);
        form.setSelected(false);
        ResponseVo<CartVo> responseVo = cartService.update(uid, productId, form);
        log.info("update: {}", gson.toJson(responseVo));
        Assertions.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());

    }

//    @AfterEach
    @Test
    void delete() {
        ResponseVo<CartVo> responseVo = cartService.delete(uid, 4);
        log.info("update: {}", gson.toJson(responseVo));
        Assertions.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());

    }

    @Test
    void selectAll() {
        ResponseVo<CartVo> responseVo = cartService.selectAll(uid);
        log.info("selectAll: {}", gson.toJson(responseVo));
        Assertions.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    @Test
    void unSelectAll() {
        ResponseVo<CartVo> responseVo = cartService.unSelectAll(uid);
        log.info("unSelectAll: {}", gson.toJson(responseVo));
        Assertions.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    @Test
    void sum() {
        ResponseVo<Integer> responseVo = cartService.sum(uid);
        log.info("sum: {}", gson.toJson(responseVo));
        Assertions.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }
}