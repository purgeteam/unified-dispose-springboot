package com.purgeteam.dispose.starter.exception;

import com.netflix.client.ClientException;
import com.purgeteam.dispose.starter.Result;
import com.purgeteam.dispose.starter.annotation.IgnoreResponseAdvice;
import com.purgeteam.dispose.starter.exception.category.BusinessException;
import com.purgeteam.dispose.starter.exception.error.CommonErrorCode;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * {@link RestControllerAdvice} 基础全局异常处理
 *
 * @author <a href="mailto:yaoonlyi@gmail.com">purgeyao</a>
 * @since 1.0.0
 */
@RestControllerAdvice
public class GlobalDefaultExceptionHandler {

    private final static Logger log = LoggerFactory.getLogger(GlobalDefaultExceptionHandler.class);

    /**
     * NoHandlerFoundException 404 异常处理
     */
    @ExceptionHandler(value = NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result handlerNoHandlerFoundException(NoHandlerFoundException e) throws Throwable {
        errorDispose(e);
        outPutErrorWarn(NoHandlerFoundException.class, CommonErrorCode.NOT_FOUND, e);
        return Result.ofFail(CommonErrorCode.NOT_FOUND);
    }

    /**
     * HttpRequestMethodNotSupportedException 405 异常处理
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result handlerHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) throws Throwable {
        errorDispose(e);
        outPutErrorWarn(HttpRequestMethodNotSupportedException.class,
                CommonErrorCode.METHOD_NOT_ALLOWED, e);
        return Result.ofFail(CommonErrorCode.METHOD_NOT_ALLOWED);
    }

    /**
     * HttpMediaTypeNotSupportedException 415 异常处理
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Result handlerHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException e) throws Throwable {
        errorDispose(e);
        outPutErrorWarn(HttpMediaTypeNotSupportedException.class,
                CommonErrorCode.UNSUPPORTED_MEDIA_TYPE, e);
        return Result.ofFail(CommonErrorCode.UNSUPPORTED_MEDIA_TYPE);
    }

    /**
     * Exception 类捕获 500 异常处理
     */
    @ExceptionHandler(value = Exception.class)
    public Result handlerException(Exception e) throws Throwable {
        errorDispose(e);
        return ifDepthExceptionType(e);
    }

    /**
     * 二次深度检查错误类型
     */
    private Result ifDepthExceptionType(Throwable throwable) throws Throwable {
        Throwable cause = throwable.getCause();
        if (cause instanceof ClientException) {
            return handlerClientException((ClientException) cause);
        }
        if (cause instanceof FeignException) {
            return handlerFeignException((FeignException) cause);
        }
        outPutError(Exception.class, CommonErrorCode.EXCEPTION, throwable);
        return Result.ofFail(CommonErrorCode.EXCEPTION);
    }

    /**
     * FeignException 类捕获
     */
    @ExceptionHandler(value = FeignException.class)
    public Result handlerFeignException(FeignException e) throws Throwable {
        errorDispose(e);
        outPutError(FeignException.class, CommonErrorCode.RPC_ERROR, e);
        return Result.ofFail(CommonErrorCode.RPC_ERROR);
    }

    /**
     * ClientException 类捕获
     */
    @ExceptionHandler(value = ClientException.class)
    public Result handlerClientException(ClientException e) throws Throwable {
        errorDispose(e);
        outPutError(ClientException.class, CommonErrorCode.RPC_ERROR, e);
        return Result.ofFail(CommonErrorCode.RPC_ERROR);
    }

    /**
     * BusinessException 类捕获
     */
    @ExceptionHandler(value = BusinessException.class)
    public Result handlerBusinessException(BusinessException e) throws Throwable {
        errorDispose(e);
        outPutError(BusinessException.class, CommonErrorCode.BUSINESS_ERROR, e);
        return Result.ofFail(e.getCode(), e.getMessage());
    }

    /**
     * HttpMessageNotReadableException 参数错误异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result handleHttpMessageNotReadableException(HttpMessageNotReadableException e) throws Throwable {
        errorDispose(e);
        outPutError(HttpMessageNotReadableException.class, CommonErrorCode.PARAM_ERROR, e);
        String msg = String.format("%s : 错误详情( %s )", CommonErrorCode.PARAM_ERROR.getMessage(),
                e.getRootCause().getMessage());
        return Result.ofFail(CommonErrorCode.PARAM_ERROR.getCode(), msg);
    }

    /**
     * ConstraintViolationException 参数错误异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result handleConstraintViolationException(ConstraintViolationException e) throws Throwable {
        errorDispose(e);
        String smg = "";
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        if (log.isDebugEnabled()) {
            for (ConstraintViolation error : constraintViolations) {
                log.error("{} -> {}", error.getPropertyPath(), error.getMessageTemplate());
                smg = error.getMessageTemplate();
            }
        }

        if (constraintViolations.isEmpty()) {
            log.error("validExceptionHandler error fieldErrors is empty");
            Result.ofFail(CommonErrorCode.BUSINESS_ERROR.getCode(), "");
        }

        return Result.ofFail(CommonErrorCode.PARAM_ERROR.getCode(), smg);
    }

    /**
     * MethodArgumentNotValidException 参数错误异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleMethodArgumentNotValidException(MethodArgumentNotValidException e) throws Throwable {
        errorDispose(e);
        BindingResult bindingResult = e.getBindingResult();
        return getBindResultDTO(bindingResult);
    }

    /**
     * BindException 参数错误异常
     */
    @ExceptionHandler(BindException.class)
    public Result handleBindException(BindException e) throws Throwable {
        errorDispose(e);
        outPutError(BindException.class, CommonErrorCode.PARAM_ERROR, e);
        BindingResult bindingResult = e.getBindingResult();
        return getBindResultDTO(bindingResult);
    }

    private Result getBindResultDTO(BindingResult bindingResult) {
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if (log.isDebugEnabled()) {
            for (FieldError error : fieldErrors) {
                log.error("{} -> {}", error.getDefaultMessage(), error.getDefaultMessage());
            }
        }

        if (fieldErrors.isEmpty()) {
            log.error("validExceptionHandler error fieldErrors is empty");
            Result.ofFail(CommonErrorCode.BUSINESS_ERROR.getCode(), "");
        }

        return Result
                .ofFail(CommonErrorCode.PARAM_ERROR.getCode(), fieldErrors.get(0).getDefaultMessage());
    }

    /**
     * 校验是否进行异常处理
     *
     * @param e   异常
     * @param <T> extends Throwable
     * @throws Throwable 异常
     */
    private <T extends Throwable> void errorDispose(T e) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HandlerMethod handlerMethod = (HandlerMethod) request.getAttribute("org.springframework.web.servlet.HandlerMapping.bestMatchingHandler");

        // 获取异常 Controller
        Class<?> beanType = handlerMethod.getBeanType();
        // 获取异常方法
        Method method = handlerMethod.getMethod();

        // 判断方法是否存在 IgnoreResponseAdvice 注解
        IgnoreResponseAdvice methodAnnotation = method.getAnnotation(IgnoreResponseAdvice.class);
        if (methodAnnotation != null) {
            // 是否使用异常处理
            if (!methodAnnotation.errorDispose()) {
                throw e;
            } else {
                return;
            }
        }
        // 判类是否存在 IgnoreResponseAdvice 注解
        IgnoreResponseAdvice classAnnotation = beanType.getAnnotation(IgnoreResponseAdvice.class);
        if (classAnnotation != null) {
            if (!classAnnotation.errorDispose()) {
                throw e;
            }
        }
    }

    public void outPutError(Class errorType, Enum secondaryErrorType, Throwable throwable) {
        log.error("[{}] {}: {}", errorType.getSimpleName(), secondaryErrorType, throwable.getMessage(),
                throwable);
    }

    public void outPutErrorWarn(Class errorType, Enum secondaryErrorType, Throwable throwable) {
        log.warn("[{}] {}: {}", errorType.getSimpleName(), secondaryErrorType, throwable.getMessage());
    }

}
