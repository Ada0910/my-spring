package com.ada.spring.framework.context;

import com.ada.spring.framework.beans.AdaBeanWrapper;
import com.ada.spring.framework.beans.config.AdaBeanDefinition;
import com.ada.spring.framework.beans.support.AdaBeanDefinitionReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ada
 * @ClassName :AdaApplicationContext
 * @date 2020/7/9 23:24
 * @Description:
 */
public class AdaApplicationContext {

    private String[] configLocations;
    private AdaBeanDefinitionReader reader;

    private final Map<String, AdaBeanDefinition> beanDefinitionMap = new HashMap<>();

    private Map<String, AdaBeanWrapper> factoryBeanInstanceCache = new HashMap<>();

    public AdaApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        //1.读取配置文件,并且都解析成BeanDefinition对象

        try {
            reader = new AdaBeanDefinitionReader(configLocations);
            List<AdaBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

            //2.把实例对应的配置信息BeanDefinition 保存到一个map中，方便之后反复读取配置信息
            doRegisterBeanDefinition(beanDefinitions);

            //3.完成getBean（）的调用，触发IOC和DI
            doCreateBean();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void doCreateBean() {
        for (Map.Entry<String, AdaBeanDefinition> beanDefinitionEntry : this.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            //真正触发IoC和DI的动作
            //创建出实例，然后进行依赖注入
            getBean(beanName);

        }
    }

    private void doRegisterBeanDefinition(List<AdaBeanDefinition> beanDefinitions) throws Exception {
        for (AdaBeanDefinition beanDefinition : beanDefinitions) {
            if (this.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception("The" + beanDefinition.getFactoryBeanName() + "is  exists!!!");
            }
            this.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
            this.beanDefinitionMap.put(beanDefinition.getBeanClassName(), beanDefinition);
        }
    }

    public Object getBean(Class beanClass) {
        return getBean(beanClass.getName());
    }

    public Object getBean(String beanName) {
        //创建实例
        //1.获取BeanDefinition的配置信息
        AdaBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);

        //2.实例化
        Object instance = instaniateBean(beanName, beanDefinition);

        //3.创建出来的实例要包装到beanWrapper
        AdaBeanWrapper beanWrapper = new AdaBeanWrapper(instance);

        //4.把BeanWrapper对象存入到IoC容器中
        factoryBeanInstanceCache.put(beanName, beanWrapper);

        //5.依赖注入
        populateBean(beanName, beanDefinition, beanWrapper);


        return this.factoryBeanInstanceCache.get(beanName).getWrapperInstance();
    }

    private void populateBean(String beanName, AdaBeanDefinition beanDefinition, AdaBeanWrapper beanWrapper) {
    }

    private Object instaniateBean(String beanName, AdaBeanDefinition beanDefinition) {
        return null;
    }
}
