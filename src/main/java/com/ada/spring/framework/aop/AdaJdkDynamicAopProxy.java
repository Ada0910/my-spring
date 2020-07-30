package com.ada.spring.framework.aop;

import com.ada.spring.framework.aop.aspect.AdaAdvice;
import com.ada.spring.framework.aop.support.AdaAdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @author Ada
 * @ClassName :AdaJdkDynamicAopProxy
 * @date 2020/7/27 23:28
 * @Description:
 */
public class AdaJdkDynamicAopProxy implements InvocationHandler {
    private AdaAdvisedSupport config;

    public AdaJdkDynamicAopProxy(AdaAdvisedSupport config) {
        this.config = config;
    }

    public Object getProxy() {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), this.config.getTargetClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //proxy 拿到代理的对象
        //method 用户调用的方法
        //args 实参
        Map<String, AdaAdvice> advices = this.config.getAdvice(method, this.config.getTargetClass());
        Object returnValue;
        invokeAdvice(advices.get("before"));
        try {
            returnValue = method.invoke(this.config.getTarget(), args);
        } catch (Exception e) {
            invokeAdvice(advices.get("afterThrowing"));
            e.printStackTrace();
            throw e;
        }
        invokeAdvice(advices.get("after"));
        return returnValue;
    }

    private void invokeAdvice(AdaAdvice advice) {
        try {
            advice.getAdviceMethod().invoke(advice.getApsect());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
