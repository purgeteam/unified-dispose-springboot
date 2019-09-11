package io.purge.starter.dispose.advice;

import com.alibaba.fastjson.JSON;
import io.purge.starter.dispose.GlobalDefaultProperties;
import io.purge.starter.dispose.Result;
import io.purge.starter.dispose.annotation.IgnorReponseAdvice;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * {@link IgnorReponseAdvice} 处理解析 {@link ResponseBodyAdvice} 统一返回包装器
 *
 * @author purgeyao
 * @since 1.0
 */
@RestControllerAdvice
public class CommonResponseDataAdvice implements ResponseBodyAdvice<Object> {

  private GlobalDefaultProperties globalDefaultProperties;

  public CommonResponseDataAdvice(GlobalDefaultProperties globalDefaultProperties) {
    this.globalDefaultProperties = globalDefaultProperties;
  }

  @Override
  @SuppressWarnings("all")
  public boolean supports(MethodParameter methodParameter,
      Class<? extends HttpMessageConverter<?>> aClass) {
    return filter(methodParameter);
  }

  @Nullable
  @Override
  @SuppressWarnings("all")
  public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType,
      Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest,
      ServerHttpResponse serverHttpResponse) {

    // o is null -> return response
    if (o == null) {
      return Result.ofSuccess();
    }
    // o is instanceof ConmmonResponse -> return o
    if (o instanceof Result) {
      return (Result<Object>) o;
    }
    // string 特殊处理
    if (o instanceof String) {
      return JSON.toJSON(Result.ofSuccess(o)).toString();
    }
    return Result.ofSuccess(o);
  }

  private Boolean filter(MethodParameter methodParameter) {
    Class<?> declaringClass = methodParameter.getDeclaringClass();
    // 检查过滤包路径
    long count = globalDefaultProperties.getAdviceFilterPackage().stream()
        .filter(l -> declaringClass.getName().contains(l)).count();
    if (count > 0) {
      return false;
    }
    // 检查<类>过滤列表
    if (globalDefaultProperties.getAdviceFilterClass().contains(declaringClass.getName())) {
      return false;
    }
    // 检查注解是否存在
    if (methodParameter.getDeclaringClass().isAnnotationPresent(IgnorReponseAdvice.class)) {
      return false;
    }
    if (methodParameter.getMethod().isAnnotationPresent(IgnorReponseAdvice.class)) {
      return false;
    }
    return true;
  }

}
