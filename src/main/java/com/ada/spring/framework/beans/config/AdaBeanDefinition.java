package com.ada.spring.framework.beans.config;

/**
 * @author Ada
 * @ClassName :AdaBeanDefinition
 * @date 2020/7/9 23:25
 * @Description:
 */
public class AdaBeanDefinition {

    private String factoryBeanName;
    private String beanClassName;

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }
}
