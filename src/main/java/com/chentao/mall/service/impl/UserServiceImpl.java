package com.chentao.mall.service.impl;

import com.chentao.mall.enums.RoleEnum;
import com.chentao.mall.mapper.UserMapper;
import com.chentao.mall.pojo.User;
import com.chentao.mall.service.IUserService;
import com.chentao.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

import static com.chentao.mall.enums.ResponseEnum.*;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    UserMapper userMapper;

    /**
     * 用户注册
     * @param user
     */
    @Override
    public ResponseVo<User> register(User user) {

        // username不能重复
        int countByUsername = userMapper.countByUsername(user.getUsername());
        System.out.println("名字的数量为：" + countByUsername);
        if (countByUsername >= 1) {
            return ResponseVo.error(USERNAME_EXIST);
        }


        // email不能重复
        int countByEmail = userMapper.countByEmail(user.getEmail());
        if (countByEmail >= 1) {
            return ResponseVo.error(EMAIL_EXIST);
        }

        user.setRole(RoleEnum.CUSTOMER.getCode());
        // 将密码用MD5加密，SpringBoot自带MD5摘要算法
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8)));


        // 写入数据库
        int insert = userMapper.insertSelective(user);
        if (insert != 1) {
            return ResponseVo.error(ERROR);
        }

        return ResponseVo.error(SUCCESS);
    }


    /**
     * 用户登录
     *
     */
    @Override
    public ResponseVo<User> login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            // 用户不存在（返回：用户名或密码错误）
            return ResponseVo.error(USERNAME_OR_PASSWORD_ERROR);
        }

        if (!user.getPassword().equalsIgnoreCase(
                DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)))) {
            // 密码错误（返回：用户名或密码错误）
            return ResponseVo.error(USERNAME_OR_PASSWORD_ERROR);
        }

        // 为了数据敏感性，在返回数据前将密码设为空字符串
        user.setPassword("");
        return ResponseVo.success(user);
    }


}
