package com.chentao.mall.service.impl;

import com.chentao.mall.mapper.ProductMapper;
import com.chentao.mall.pojo.Product;
import com.chentao.mall.service.ICategoryService;
import com.chentao.mall.service.IProductService;
import com.chentao.mall.vo.ProductDetailVo;
import com.chentao.mall.vo.ProductVo;
import com.chentao.mall.vo.ResponseVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.chentao.mall.enums.ProductStatusEnum.DELETE;
import static com.chentao.mall.enums.ProductStatusEnum.OFF_SALE;
import static com.chentao.mall.enums.ResponseEnum.PRODUCT_OF_SALE_OR_DELETE;

@Service
@Slf4j
public class ProductServiceImpl implements IProductService {
    @Autowired
    ProductMapper productMapper;
    @Autowired
    ICategoryService categoryService;

    @Override
    public ResponseVo<PageInfo> list(Integer categoryId, Integer pageNum, Integer pageSize) {
        Set<Integer> categoryIdSet = new HashSet<>();
        if (categoryId != null) {
            categoryService.findSubCategoryId(categoryId, categoryIdSet);
            categoryIdSet.add(categoryId);
        }

        PageHelper.startPage(pageNum, pageSize);

        List<Product> productList = productMapper.selectByCategoryIdSet(categoryIdSet);
        List<ProductVo> productVoList = productList.stream()
                .map(e -> {
                    ProductVo productVo = new ProductVo();
                    BeanUtils.copyProperties(e, productVo);
                    return productVo;
                })
                .collect(Collectors.toList());

        PageInfo pageInfo = new PageInfo<>(productList);
        pageInfo.setList(productVoList);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<ProductDetailVo> detail(Integer productId) {
        Product product = productMapper.selectByPrimaryKey(productId);

        // 如果商品处于下架状态或删除状态，就返回错误
        // 只对确定的状态进行判断，将增强程序的可扩展性
        if(product.getStatus().equals(OFF_SALE) || product.getStatus().equals(DELETE)) {
            return ResponseVo.error(PRODUCT_OF_SALE_OR_DELETE);
        }

        ProductDetailVo productDetailVo = new ProductDetailVo();
        BeanUtils.copyProperties(product, productDetailVo);

        // 对敏感数据的处理，如果库存大于等于100件就显示为100件，否则就显示真实的库存值
        productDetailVo.setStock(product.getStock() >= 100 ? 100 : product.getStock());

        return ResponseVo.success(productDetailVo);
    }

}
