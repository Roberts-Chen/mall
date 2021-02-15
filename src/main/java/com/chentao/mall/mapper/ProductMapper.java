package com.chentao.mall.mapper;

import com.chentao.mall.pojo.Product;
import org.apache.ibatis.annotations.MapKey;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> selectByCategoryIdSet(Set<Integer> categoryIdSet);

    @MapKey("id")
    Map<Integer, Product> selectByProductIdSet(Set<Integer> productIdSet);

}