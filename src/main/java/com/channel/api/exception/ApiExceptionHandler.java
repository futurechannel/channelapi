package com.channel.api.exception;

import com.channel.api.dto.BaseResult;
import com.channel.api.enums.ErrorCode;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * User:甘琪 DateTime: 2016/6/6.
 */
@ControllerAdvice
public class ApiExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);


    @ExceptionHandler(ApiException.class)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public BaseResult handleInvalidRequestError(ApiException ex) {
        Cat.logEvent("Exception", "api", Message.SUCCESS, "code_" + ex.getMessage());
        return new BaseResult(Integer.parseInt(ex.getMessage()));
    }


    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public BaseResult handleUnexpectedServerError(RuntimeException ex) {
        logger.error("未知异常:", ex);
        Cat.logError("未知异常:", ex);
        Cat.logEvent("Exception", "api", Message.SUCCESS, "code_" + ErrorCode.E500.getCode());
        return new BaseResult(ErrorCode.E500.getCode());
    }

}
