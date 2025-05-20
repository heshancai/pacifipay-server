package com.starchain.exception;

/**
 * @author
 * @date 2025-05-19
 * @Description
 */

import com.starchain.common.result.ClientResponse;
import com.starchain.common.result.ResultGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器，用于统一处理 Controller 层抛出的异常。
 * 特别是参数校验失败时的异常（如 @Valid 校验不通过）。
 */
@RestControllerAdvice // 是 @ControllerAdvice 和 @ResponseBody 的组合注解，表示该类是一个全局异常处理器，并且返回值直接作为响应体。
@Slf4j
public class BaseGlobalControllerHandler {

    /**
     * 处理参数校验失败的异常（MethodArgumentNotValidException）
     * 即 Spring MVC 中由 @Valid 触发的参数校验失败异常。，会进入此方法
     *
     * @param ex 抛出的 MethodArgumentNotValidException 异常对象
     * @return 返回统一格式的错误响应 ClientResponse
     */
    @ExceptionHandler(MethodArgumentNotValidException.class) //  用于定义异常处理方法
    public ClientResponse handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error(ex.getMessage());
        // 从异常中提取所有的校验错误信息
        String errorMessage = ex.getBindingResult()
                .getAllErrors() //  获取所有字段的校验错误
                .stream() //  转换为流，便于操作
                .map(DefaultMessageSourceResolvable::getDefaultMessage) //  提取每个错误的默认提示信息
                .findFirst() //  取第一个错误信息作为返回内容
                .orElse("参数校验失败"); //  如果没有具体错误，返回默认提示
        // 打印日志到控制台或文件（方便开发排查问题）
        log.error("URL:{} ,绑定异常:{} ", request.getRequestURI(),errorMessage);
        // 返回客户端友好的错误信息（前端可识别的格式）
        return ResultGenerator.genFailResult(errorMessage); //  将错误信息封装成 ClientResponse 返回给前端
    }
}
