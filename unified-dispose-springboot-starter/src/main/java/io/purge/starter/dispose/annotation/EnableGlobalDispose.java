package io.purge.starter.dispose.annotation;

import io.purge.starter.dispose.GlobalDefaultConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * @author purgeyao
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(GlobalDefaultConfiguration.class)
public @interface EnableGlobalDispose {

}
