package com.chentao.mall.service;

import com.chentao.mall.vo.CategoryVo;
import com.chentao.mall.vo.ResponseVo;

import java.util.List;
import java.util.Set;

public interface ICategoryService {
    /**
     * 查询所有的分类
     * @return
     */
    ResponseVo<List<CategoryVo>> selectAll();

    /**
     * 查询所有的子分类id，通过分类id查询商品列表时有用，这会帮我们查询该分类下的所有子分类id
     * 然后通过这个分类id的集合，查询出所有的相关商品
     */
    void findSubCategoryId(Integer id, Set<Integer> resultSet);
}
