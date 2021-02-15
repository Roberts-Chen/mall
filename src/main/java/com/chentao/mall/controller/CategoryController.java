package com.chentao.mall.controller;

import com.chentao.mall.service.ICategoryService;
import com.chentao.mall.vo.CategoryVo;
import com.chentao.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {
    @Autowired
    ICategoryService categoryService;


    @GetMapping("/categories")
    public ResponseVo<List<CategoryVo>> categories() {
        return categoryService.selectAll();
    }
    
}
