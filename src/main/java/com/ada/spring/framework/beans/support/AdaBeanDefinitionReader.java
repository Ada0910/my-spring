package com.ada.spring.framework.beans.support;

import com.ada.spring.framework.beans.config.AdaBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Ada
 * @ClassName :AdaBeanDefinitionReader
 * @date 2020/7/9 23:26
 * @Description:
 */
public class AdaBeanDefinitionReader {
    private Properties contextConfig = new Properties();

    private List<String> registryBeanClasses = new ArrayList<>();

    public AdaBeanDefinitionReader(String[] configLocations) {
        //1.读取配置文件
        doLoadConfig(configLocations[0]);

        //2.扫描相关的类
        //  doScanner(contextConfig.getProperty("scanPackage"));
        doScanner("com.ada.demo");
    }

    public List<AdaBeanDefinition> loadBeanDefinitions() {
        List<AdaBeanDefinition> result = new ArrayList<>();
        try {
            for (String className : registryBeanClasses) {
                Class<?> beanClass = Class.forName(className);
                //如果是接口
                if (beanClass.isInterface()) {
                    continue;
                }
                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));

                //如果在DI时字段的类型是接口，那么我们读取它的实现类的配置
                for (Class<?> i : beanClass.getInterfaces()) {
                    result.add(doCreateBeanDefinition(i.getName(), beanClass.getName()));
                }

            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    private AdaBeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName) {
        AdaBeanDefinition beanDefinition = new AdaBeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;
    }

    private void doScanner(String scanPackage) {
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles()) {

            //判断是否是文件夹，如果不是的话，递归
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                //拿到全类名
                String className = (scanPackage + "." + file.getName().replace(".class", ""));
                registryBeanClasses.add(className);
            }

        }
    }

    private void doLoadConfig(String contextConfigLocation) {
        //从classpath路径读取配置文件到内存中
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
        try {
            if (is != null) {
                contextConfig.load(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    //首字母小写
    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }


    public Properties getConfig() {
        return this.contextConfig;
    }
}
