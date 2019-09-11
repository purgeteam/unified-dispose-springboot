package com.purgeteam.demo;

import io.purge.starter.dispose.annotation.EnableGlobalDispose;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author purgeyao
 * @since 1.0
 */
@EnableGlobalDispose
@SpringBootApplication
public class DisposeDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DisposeDemoApplication.class, args);
    }

}
