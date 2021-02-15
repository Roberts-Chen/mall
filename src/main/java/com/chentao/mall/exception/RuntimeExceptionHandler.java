package com.chentao.mall.exception;

import com.chentao.mall.vo.ResponseVo;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

import static com.chentao.mall.enums.ResponseEnum.*;

@RestControllerAdvice
public class RuntimeExceptionHandler {

    /**
     * 处理RuntimeException异常，统一json的数据格式
     *
     * @param e 异常信息
     * @return 返回json数据提示错误信息
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseVo runtimeExceptionHandle(RuntimeException e) {
        return ResponseVo.error(ERROR, e.getMessage());
    }

    /**
     * 当用户未登录时抛出该异常
     * @return 返回json数据提示未登录
     */
    @ExceptionHandler(UserLoginException.class)
    public ResponseVo userLoginExceptionHandle() {
        return ResponseVo.error(NEED_LOGIN);
    }

    /**
     * 表单统一校验之后的异常处理函数，返回指定的数据格式
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseVo methodArgumentNotValidExceptionHandle(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        return ResponseVo.error(PARAM_ERROR, Objects.requireNonNull(bindingResult.getFieldError()).getField() + " " + bindingResult.getFieldError().getDefaultMessage());
    }
}
