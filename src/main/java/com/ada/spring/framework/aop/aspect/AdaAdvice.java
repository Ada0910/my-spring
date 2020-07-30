package com.ada.spring.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @author Ada
 * @ClassName :AdaAdvice
 * @date 2020/7/27 23:33
 * @Description:
 */
public class AdaAdvice {
    private Object apsect;
    private Method adviceMethod;
    private String throwName;

    public AdaAdvice(Object aspect, Method adviceMethod) {
        this.apsect = aspect;
        this.adviceMethod = adviceMethod;
    }

    public Object getApsect() {
        return apsect;
    }

    public void setApsect(Object apsect) {
        this.apsect = apsect;
    }

    public Method getAdviceMethod() {
        return adviceMethod;
    }

    public void setAdviceMethod(Method adviceMethod) {
        this.adviceMethod = adviceMethod;
    }

    public String getThrowName() {
        return throwName;
    }

    public void setThrowName(String throwName) {
        this.throwName = throwName;
    }
}
