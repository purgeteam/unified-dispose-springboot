package io.purge.starter.dispose;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author purgeyao
 * @since 1.0
 */
@Data
@ConfigurationProperties(GlobalDefaultProperties.PREFIX)
public class GlobalDefaultProperties {

  public static final String PREFIX = "dispose";

  /**
   * 统一返回过滤包
   */
  private List<String> adviceFilterPackage = new ArrayList<>();

  /**
   * 统一返回过滤类
   */
  private List<String> adviceFilterClass = new ArrayList<>();

}
