package com.purgeteam.demo;

import io.purge.starter.dispose.annotation.IgnorReponseAdvice;
import io.purge.starter.dispose.exception.category.BusinessException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author purgeyao
 * @since 1.0
 */
@RestController
public class DemoController {

    @GetMapping("test")
    public String test() {
        return "test";
    }

    /**
     * {@link IgnorReponseAdvice} 使用
     */
    @IgnorReponseAdvice
    @GetMapping("ignor")
    public String ignor() {
        return "ignor";
    }

    /**
     * 全局异常处理
     */
    @GetMapping("businessError")
    public String error() {
        throw new BusinessException("0","异常演示");
    }
}
