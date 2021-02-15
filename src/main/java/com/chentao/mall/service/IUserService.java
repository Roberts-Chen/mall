package com.chentao.mall.service;

import com.chentao.mall.pojo.User;
import com.chentao.mall.vo.ResponseVo;


public interface IUserService {
    /**
     * 注册
     */
    public ResponseVo register(User user);


    /**
     * 登录
     */
    public ResponseVo login(String username, String password);
}
