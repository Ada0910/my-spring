package com.ada.spring.framework.webmvc.servlet;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Ada
 * @ClassName :AdaHandlerMapping
 * @date 2020/7/14 23:36
 * @Description:
 */
public class AdaHandlerMapping {

    private Object controller;
    private Method method;
    private Pattern pattern;

    public AdaHandlerMapping(Pattern pattern, Object instance, Method method) {
        this.controller = instance;
        this.method = method;
        this.pattern = pattern;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
}
