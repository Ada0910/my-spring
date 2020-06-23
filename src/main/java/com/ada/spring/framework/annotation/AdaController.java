package com.ada.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @author Ada
 * @ClassName :AdaController
 * @date 2020/6/23 22:55
 * @Description:
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AdaController {
    String value() default  "";
}
