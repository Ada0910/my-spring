package com.ada.spring.framework.aop.support;

import com.ada.spring.framework.aop.config.AdaAopConfig;

/**
 * @author Ada
 * @ClassName :AdaAdvisedSupport
 * @date 2020/7/27 23:29
 * @Description:
 */
public class AdaAdvisedSupport {
    //解析读取出来的配置信息
    public AdaAdvisedSupport(AdaAopConfig config) {
    }

    public boolean pointCutMatch() {
        return false;
    }

    public void setTargetClass(Class<?> clazz) {
    }

    public void setTarget(Object instance) {
    }
}
