package com.chentao.mall.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserRegisterForm {
    // 使用@NotBlank进行数据校验，@NotBlank会将传入的空字符串视作非法
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String email;
}
