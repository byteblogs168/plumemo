package com.byteblogs.common.handler;

import com.byteblogs.common.base.domain.Result;
import com.byteblogs.common.enums.ErrorEnum;
import com.byteblogs.common.exception.ApiInvalidParamException;
import com.byteblogs.common.exception.BusinessException;
import com.byteblogs.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @Author:byteblogs
 * @Date:2018/08/20 18:49
 */
@Slf4j
public class HandlerExceptionResolver implements org.springframework.web.servlet.HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) {
        log.error("系统统一异常处理：", exception);

        // 若响应已响应或已关闭，则不操作
        if (response.isCommitted()) {
            return new ModelAndView();
        }

        // 组装错误提示信息
        String errorCode = exception instanceof BusinessException ? ((BusinessException) exception).getCode() : ErrorEnum.ERROR.getCode();
        String message = ErrorEnum.getErrorEnumMap(errorCode).getZhMsg();

        if (exception instanceof ApiInvalidParamException) {
            //定义错误编码
            //errorCode = 10001;
            message = exception.getMessage();
        }
        // 响应类型设置
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

        // 响应结果输出
        try (PrintWriter writer = response.getWriter()) {
            writer.write(JsonUtil.toJsonString(Result.createWithErrorMessage(message, errorCode)));
        } catch (Exception e) {
            log.error("响应输出失败！原因如下：", e);
        }
        return new ModelAndView();
    }
}