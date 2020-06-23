package com.ada.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @author Ada
 * @ClassName :AdaAutowired
 * @date 2020/6/23 22:52
 * @Description: 自动注入
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AdaAutowired {
    String value() default "";
}
