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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import static com.chentao.mall.enums.ResponseEnum.PARAM_ERROR;

@Slf4j
@RestController
//@RequestMapping("/user")
public class UserController {
    @Autowired
    IUserService userService;

    /**
     * 用户注册
     */
    @ResponseBody
    @PostMapping(value = "/user/register", produces = "application/json")
    public ResponseVo register(@Valid @RequestBody UserRegisterForm userRegisterForm,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("注册的参数有误，{} {}",
                    bindingResult.getFieldError().getField(),
                    bindingResult.getFieldError().getDefaultMessage());
            return ResponseVo.error(PARAM_ERROR, bindingResult);
        }

        User user = new User();
        BeanUtils.copyProperties(userRegisterForm, user);
        return userService.register(user);
    }

    @PostMapping("/user/login")
    public ResponseVo login(@Valid @RequestBody UserLoginForm userLoginForm,
                            BindingResult bindingResult,
                            HttpSession session) {
        if (bindingResult.hasErrors()) {
            return ResponseVo.error(PARAM_ERROR, bindingResult);
        }

        ResponseVo<User> userResponseVo = userService.login(userLoginForm.getUsername(), userLoginForm.getPassword());
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
        log.info("/user/logout sessionId={}", session.getId());
        // 从session中删除用户信息
        session.removeAttribute(MallConst.CURRENT_USER);
        return ResponseVo.success();
    }
}
