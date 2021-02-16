package com.chentao.mall.service.impl;

import com.chentao.mall.MallApplicationTests;
import com.chentao.mall.enums.ResponseEnum;
import com.chentao.mall.enums.RoleEnum;
import com.chentao.mall.pojo.User;
import com.chentao.mall.service.IUserService;
import com.chentao.mall.vo.ResponseVo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
class UserServiceImplTest extends MallApplicationTests {
    @Autowired
    IUserService userService;

    private static String USERNAME = "谭露露";
    private static String PASSWORD = "123456";
    private static String EMAIL = "225772086@qq.com";

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

//    @BeforeAll必须加在静态方法上，用于进行某些静态初始化
//    @BeforEach必须加在非静态方法上，在每个测试方法之前执行
//    @BeforeEach
    @Test
    void register() {
        ResponseVo<User> responseVo = userService.register(
                new User(
                        USERNAME,
                        PASSWORD,
                        EMAIL,
                        RoleEnum.CUSTOMER.getCode()
                )
        );
        log.info("register: {}", gson.toJson(responseVo));
        Assertions.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

    @Test
    void login() {
        register();
        ResponseVo<User> responseVo = userService.login(USERNAME, PASSWORD);
        log.info("register: {}", gson.toJson(responseVo));
        Assertions.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }

}