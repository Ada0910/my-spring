package com.ada.spring.framework.beans;

/**
 * @author Ada
 * @ClassName :AdaBeanWrapper
 * @date 2020/7/9 23:26
 * @Description:
 */
public class AdaBeanWrapper {

    private Object wrapperInstance;
    private Class<?> wrapperClass;

    public AdaBeanWrapper(Object wrapperInstance) {
        this.wrapperClass = wrapperInstance.getClass();
        this.wrapperInstance = wrapperInstance;
    }

    public Object getWrapperInstance() {
        return wrapperInstance;
    }

    public Class<?> getWrapperClass() {
        return wrapperClass;
    }
}
