package com.chentao.mall.service.impl;

import com.chentao.mall.MallApplicationTests;
import com.chentao.mall.enums.ResponseEnum;
import com.chentao.mall.service.IProductService;
import com.chentao.mall.vo.ProductDetailVo;
import com.chentao.mall.vo.ResponseVo;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
class ProductServiceImplTest extends MallApplicationTests {

    @Autowired
    IProductService productService;

    private static Integer CATEGORY_ID = 3;
    private static Integer PAGE_SIZE = 5;
    private static Integer PAGE_NUM = 1;

    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();



    @Test
    void list() {
        ResponseVo<PageInfo> responseVo = productService.list(CATEGORY_ID, PAGE_NUM, PAGE_SIZE);
        log.info("list: {}", gson.toJson(responseVo));
        Assertions.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }


    @Test
    void detail() {
        ResponseVo<ProductDetailVo> responseVo = productService.detail(1);
        log.info("detail: {}", gson.toJson(responseVo));
        Assertions.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }
}