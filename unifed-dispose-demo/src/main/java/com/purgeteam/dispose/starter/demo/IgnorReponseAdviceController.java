package com.purgeteam.dispose.starter.demo;

import com.purgeteam.dispose.starter.annotation.IgnorReponseAdvice;
import com.purgeteam.dispose.starter.exception.category.BusinessException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类忽略拦截测试
 *
 * @author <a href="mailto:yaoonlyi@gmail.com">purgeyao</a>
 * @since 1.0.0
 */
@RestController
@IgnorReponseAdvice(errorDispose = false)
@RequestMapping("ignor")
public class IgnorReponseAdviceController {

    /**
     * 全局异常处理
     */
    @GetMapping("error")
    public String error() {
        throw new BusinessException("0", "异常演示");
    }

    /**
     * 全局异常处理
     */
    @GetMapping("error1")
//    @IgnorReponseAdvice
    public String error1() {
        throw new BusinessException("0", "异常演示");
    }

}
