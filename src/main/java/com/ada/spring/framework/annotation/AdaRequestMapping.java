package com.ada.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @author Ada
 * @ClassName :AdaRequestMapping
 * @date 2020/6/23 23:03
 * @Description:
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AdaRequestMapping {
    String value() default "";
}
