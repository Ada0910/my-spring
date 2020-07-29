package com.ada.spring.framework.aop;

import com.ada.spring.framework.aop.aspect.AdaAdvice;
import com.ada.spring.framework.aop.support.AdaAdvisedSupport;

import java.lang.reflect.InvocationHandler;
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

        Map<String, AdaAdvice> advices = this.config.getAdvice(method, this.config.getTargetClass());
        advices.get("before");
        try {
            method.invoke(this.config.getTarget(), args);
        } catch (Exception e) {
            advices.get("afterThrowing");
            e.printStackTrace();
            throw e;
        }

        advices.get("after");
        return null;
    }
}
