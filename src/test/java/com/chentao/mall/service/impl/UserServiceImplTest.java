package com.chentao.mall.service.impl;

import com.chentao.mall.MallApplicationTests;
import com.chentao.mall.enums.ResponseEnum;
import com.chentao.mall.enums.RoleEnum;
import com.chentao.mall.pojo.User;
import com.chentao.mall.service.IUserService;
import com.chentao.mall.vo.ResponseVo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

//@Transactional
//@Rollback
class UserServiceImplTest extends MallApplicationTests {
    @Autowired
    IUserService userService;

    private static String USERNAME = "陈涛";
    private static String PASSWORD = "123456";
    private static String EMAIL = "2257720867@qq.com";

//    @BeforeAll必须加在静态方法上，用于进行某些静态初始化
//    @BeforEach必须加在非静态方法上，在每个测试方法之前执行
//    @BeforeEach
    @Test
    void register() {
        ResponseVo responseVo = userService.register(
                new User("陈涛",
                         "123456",
                         "2257720867@qq.com",
                        RoleEnum.CUSTOMER.getCode()));
        Assertions.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    @Test
    void login() {
        ResponseVo login = userService.login(USERNAME, PASSWORD);
        Assertions.assertEquals(ResponseEnum.SUCCESS.getCode(), login.getStatus());
    }

}