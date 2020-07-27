package com.ada.spring.framework.context;

import com.ada.spring.framework.annotation.AdaAutowired;
import com.ada.spring.framework.annotation.AdaController;
import com.ada.spring.framework.annotation.AdaService;
import com.ada.spring.framework.beans.AdaBeanWrapper;
import com.ada.spring.framework.beans.config.AdaBeanDefinition;
import com.ada.spring.framework.beans.support.AdaBeanDefinitionReader;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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

    private Map<String, Object> factoryBeanObjectCache = new HashMap<>();

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
        Object instance = beanWrapper.getWrapperInstance();
        Class<?> clazz = beanWrapper.getWrapperClass();

        //只有加了注解的类才要依赖注入
        if (clazz.isAnnotationPresent(AdaController.class) || clazz.isAnnotationPresent(AdaService.class)) {
            return;
        }

        for (Field field : clazz.getDeclaredFields()) {
            //只有加了个注解的才给你赋值
            if (!field.isAnnotationPresent(AdaAutowired.class)) {
                continue;
            }
            AdaAutowired autowired = field.getAnnotation(AdaAutowired.class);
            String autowiredBeanName = autowired.value().trim();
            if (autowiredBeanName.equals("")) {
                autowiredBeanName = field.getType().getName();
            }
            //强制暴力访问
            field.setAccessible(true);
            try {
                //ield相当于@AdaAutowired QueryService queryService;
                //entry.getValue()相当于 MyAction的实例
                //ioc.get(beanName)相当于ioc.get("com.ada.demo.service");
                //总体这句话的意思就是拿到了对应的实例赋值给QueryService
                // field.set(entry.getValue(), ioc.get(beanName));
                if (factoryBeanInstanceCache.get(autowiredBeanName) == null) {
                    continue;
                }
                field.set(instance, this.factoryBeanInstanceCache.get(autowiredBeanName).getWrapperInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    private Object instaniateBean(String beanName, AdaBeanDefinition beanDefinition) {
        String className = beanDefinition.getBeanClassName();
        Object instance = null;
        try {
            Class<?> clazz = Class.forName(className);
            instance = clazz.newInstance();

            //此处应该有AOP的介入




            factoryBeanObjectCache.put(beanName, instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }


    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }


    public String[] getBeanDefinitionName() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public Properties getConfig() {
        return  reader.getConfig();
    }
}