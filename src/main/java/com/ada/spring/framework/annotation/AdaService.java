package com.ada.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @author Ada
 * @ClassName :AdaService
 * @date 2020/6/23 23:08
 * @Description:
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AdaService {
    String value() default "";
}
