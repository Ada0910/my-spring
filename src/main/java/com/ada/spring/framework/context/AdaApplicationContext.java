package com.ada.spring.framework.context;

import com.ada.spring.framework.beans.config.AdaBeanDefinition;
import com.ada.spring.framework.beans.support.AdaBeanDefinitionReader;

import java.util.List;

/**
 * @author Ada
 * @ClassName :AdaApplicationContext
 * @date 2020/7/9 23:24
 * @Description:
 */
public class AdaApplicationContext {

    private String[] configLocations;
    private AdaBeanDefinitionReader reader;

    public AdaApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        //1.读取配置文件,并且都解析成BeanDefinition对象
        reader = new AdaBeanDefinitionReader(configLocations);
        List<AdaBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
    }

    public Object getBean(Class beanClass) {
        return getBean(beanClass.getName());
    }

    public Object getBean(String beanName) {
        return null;
    }
}
