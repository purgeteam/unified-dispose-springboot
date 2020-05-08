package com.purgeteam.dispose.starter.demo;

import com.purgeteam.dispose.starter.annotation.IgnoreResponseAdvice;
import com.purgeteam.dispose.starter.exception.category.BusinessException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author purgeyao
 * @since 1.0
 */
@RestController
public class DemoController {

    /**
     * 测试 返回为 string类型 值为 不为null
     */
    @GetMapping("test")
    public String test() {
        return "test";
    }

    /**
     * 测试 返回为 String类型 值为 null
     */
    @GetMapping("test-null")
    public String testNull() {
        return null;
    }

    /**
     * 测试 返回为 object类型 值为 null
     */
    @GetMapping("object-null")
    public Object objectNull() {
        return null;
    }

    /**
     * 测试 返回为 list类型 值为 null
     */
    @GetMapping("test-list-null")
    public List<String> testListNull() {
        return null;
    }

    /**
     * {@link IgnoreResponseAdvice} 使用
     */
    @IgnoreResponseAdvice
    @GetMapping("ignor")
    public String ignor() {
        return "ignor";
    }

    /**
     * 全局异常处理
     */
    @GetMapping("businessError")
    public String error() {
        throw new BusinessException("0", "异常演示");
    }
}
