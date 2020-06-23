package com.ada.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @author Ada
 * @ClassName :AdaRequestParam
 * @date 2020/6/23 23:05
 * @Description:
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AdaRequestParam {
    String value() default "";

    boolean required() default true;
}
