package io.purge.starter.dispose;

import io.purge.starter.dispose.advice.CommonResponseDataAdvice;
import io.purge.starter.dispose.exception.GlobalDefaultExceptionHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author purgeyao
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties(GlobalDefaultProperties.class)
@PropertySource(value = "classpath:dispose.properties", encoding = "UTF-8")
public class GlobalDefaultConfiguration {

  @Bean
  public GlobalDefaultExceptionHandler globalDefaultExceptionHandler() {
    return new GlobalDefaultExceptionHandler();
  }

  @Bean
  public CommonResponseDataAdvice commonResponseDataAdvice(GlobalDefaultProperties globalDefaultProperties){
    return new CommonResponseDataAdvice(globalDefaultProperties);
  }

}
