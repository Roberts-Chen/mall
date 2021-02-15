package com.chentao.mall.service;

import com.chentao.mall.form.ShippingForm;
import com.chentao.mall.vo.ResponseVo;
import com.github.pagehelper.PageInfo;

import java.util.Map;

public interface IShippingService {

    public ResponseVo<Map<String, Integer>> add(Integer uid, ShippingForm form);

    ResponseVo delete(Integer uid, Integer shippingId);

    ResponseVo update(Integer uid, Integer shippingId, ShippingForm form);

    ResponseVo<PageInfo> list(Integer uid, Integer pageNum, Integer pageSize);
}
