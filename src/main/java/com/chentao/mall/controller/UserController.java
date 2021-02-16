package com.chentao.mall.controller;

import com.chentao.mall.consts.MallConst;
import com.chentao.mall.form.UserLoginForm;
import com.chentao.mall.form.UserRegisterForm;
import com.chentao.mall.pojo.User;
import com.chentao.mall.service.IUserService;
import com.chentao.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Slf4j
@RestController
public class UserController {
    @Autowired
    IUserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/user/register")
    public ResponseVo<User> register(@Valid @RequestBody UserRegisterForm form) {
        User user = new User();
        BeanUtils.copyProperties(form, user);
        return userService.register(user);
    }

    @PostMapping("/user/login")
    public ResponseVo<User> login(@Valid @RequestBody UserLoginForm form,
                            HttpSession session) {
        ResponseVo<User> userResponseVo = userService.login(form.getUsername(),
                                                            form.getPassword());
        session.setAttribute(MallConst.CURRENT_USER, userResponseVo.getData());
        return userResponseVo;
    }

    @GetMapping("/user")
    public ResponseVo<User> userInfo(HttpSession session) {
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return ResponseVo.success(user);
    }

    @PostMapping("/user/logout")
    public ResponseVo logout(HttpSession session) {
//        log.info("/user/logout sessionId={}", session.getId());
        // 从session中删除用户信息
        session.removeAttribute(MallConst.CURRENT_USER);
        return ResponseVo.success();
    }
}
