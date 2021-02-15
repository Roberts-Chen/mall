package com.chentao.mall.service.impl;

import com.chentao.mall.MallApplicationTests;
import com.chentao.mall.enums.ResponseEnum;
import com.chentao.mall.service.ICategoryService;
import com.chentao.mall.vo.CategoryVo;
import com.chentao.mall.vo.ResponseVo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
class CategoryServiceImplTest extends MallApplicationTests {
    @Autowired
    ICategoryService categoryService;

    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @Test
    void selectAll() {
        ResponseVo<List<CategoryVo>> responseVo = categoryService.selectAll();
        log.info("selectAll: {}", gson.toJson(responseVo));
        Assertions.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    @Test
    void findSubCategoryId() {
        Set<Integer> resultSet = new HashSet<>();
        categoryService.findSubCategoryId(3, resultSet);
        log.info("resultSet={}", gson.toJson(resultSet));
    }
}