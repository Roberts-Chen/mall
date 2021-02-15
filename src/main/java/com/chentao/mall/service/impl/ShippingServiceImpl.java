package com.chentao.mall.service.impl;

import com.chentao.mall.enums.ResponseEnum;
import com.chentao.mall.form.ShippingForm;
import com.chentao.mall.mapper.ShippingMapper;
import com.chentao.mall.pojo.Shipping;
import com.chentao.mall.service.IShippingService;
import com.chentao.mall.vo.ResponseVo;
import com.chentao.mall.vo.ShippingVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    ShippingMapper shippingMapper;

    @Override
    public ResponseVo<Map<String, Integer>> add(Integer uid, ShippingForm form) {
        Shipping shipping = new Shipping();
        BeanUtils.copyProperties(form, shipping);
        shipping.setUserId(uid);
        int row = shippingMapper.insertSelective(shipping);
        if (row == 0) {
            return ResponseVo.error(ResponseEnum.ERROR);
        }
        Map<String, Integer> res = new HashMap<>();
        res.put("shippingId", shipping.getId());
        return ResponseVo.success(res);
    }

    @Override
    public ResponseVo delete(Integer uid, Integer shippingId) {
        int row = shippingMapper.deleteByIdAndUid(uid, shippingId);
        if (row == 0) {
            return ResponseVo.error(ResponseEnum.DELETE_SHIPPING_FAIL);
        }
        return ResponseVo.success();
    }

    @Override
    public ResponseVo update(Integer uid, Integer shippingId, ShippingForm form) {
        Shipping shipping = new Shipping();
        BeanUtils.copyProperties(form, shipping);
        shipping.setUserId(uid);
        shipping.setId(shippingId);
        int row = shippingMapper.updateByPrimaryKeySelective(shipping);
        if (row == 0) {
            return ResponseVo.error(ResponseEnum.ERROR);
        }
        return ResponseVo.success();
    }

    @Override
    public ResponseVo<PageInfo> list(Integer uid, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUid(uid);
        List<ShippingVo> shippingVoList = shippingList.stream()
                .map(e -> {
                    ShippingVo shippingVo = new ShippingVo();
                    BeanUtils.copyProperties(e, shippingVo);
                    return shippingVo;
                })
                .collect(Collectors.toList());
        PageInfo pageInfo = new PageInfo<>(shippingList);
        pageInfo.setList(shippingVoList);
        return ResponseVo.success(pageInfo);
    }
}
