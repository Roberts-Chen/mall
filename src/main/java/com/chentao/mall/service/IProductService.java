package com.chentao.mall.service;

import com.chentao.mall.vo.ProductDetailVo;
import com.chentao.mall.vo.ResponseVo;
import com.github.pagehelper.PageInfo;

public interface IProductService {
    /**
     * 查询商品列表
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResponseVo<PageInfo> list(Integer categoryId, Integer pageNum, Integer pageSize);

    /**
     * 查询某个商品的细节
     * @param categoryId
     * @return
     */
    ResponseVo<ProductDetailVo> detail(Integer categoryId);
}
