package com.ada.spring.framework.webmvc.servlet;


import com.ada.spring.framework.annotation.AdaAutowired;
import com.ada.spring.framework.annotation.AdaController;
import com.ada.spring.framework.annotation.AdaRequestMapping;
import com.ada.spring.framework.annotation.AdaService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * @author Ada
 * @ClassName :AdaDispatcherServlet
 * @date 2020/6/22 23:42
 * @Description:
 */
public class AdaDispatcherServlet extends HttpServlet {

    //2.初始化IoC容器
    private Map<String, Object> ioc = new HashMap<String, Object>();

    private Properties contextConfig = new Properties();

    private List<String> classNames = new ArrayList<>();

    private Map<String, Method> handlerMapping = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //7.调用
        doDispatch();
    }

    private void doDispatch() {
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //1.加载配置
        doLoadConfig(config.getInitParameter("contextConfigLocation"));

        //3.扫描相关的类
        doScanner(contextConfig.getProperty("scanPackage"));

        //4.实例化扫描到的类并且缓存到IOC容器中
        doInstance();

        //5.完成依赖注入
        doAutowired();

        //6.初始化handlerMapping
        doHandlerMapping();

        //////////////////初始化阶段完成/////////////////
        System.out.println("My Spring framework is init .........");
    }

    /**
     * 初始化handlerMapping
     */
    private void doHandlerMapping() {
        if (ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {

            Class<?> clazz = entry.getValue().getClass();

            for (Method method : clazz.getMethods()) {
                if (!method.isAnnotationPresent(AdaRequestMapping.class)) {
                    continue;
                }
                AdaRequestMapping requestMapping = method.getAnnotation(AdaRequestMapping.class);
                String url = requestMapping.value();
                handlerMapping.put(url, method);

            }

        }
    }

    /**
     * 注入
     */
    private void doAutowired() {
        if (ioc.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            //获取所有字段
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields) {
                //只有加了个注解的才给你赋值
                if (!field.isAnnotationPresent(AdaAutowired.class)) {
                    continue;
                }
                AdaAutowired autowired = field.getAnnotation(AdaAutowired.class);
                String beanName = autowired.value().trim();
                if (beanName.equals("")) {
                    beanName = field.getType().getName();
                }
                //强制暴力访问
                field.setAccessible(true);
                try {
                    //ield相当于@AdaAutowired QueryService queryService;
                    //entry.getValue()相当于 MyAction的实例
                    //ioc.get(beanName)相当于ioc.get("com.ada.demo.service");
                    //总体这句话的意思就是拿到了对应的实例赋值给QueryService
                    field.set(entry.getValue(), ioc.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    continue;
                }

            }
        }

    }

    /**
     * 实例化
     */
    private void doInstance() {
        //判断实例是否为空
        if (classNames.isEmpty()) {
            return;
        }
        try {
            for (String className : classNames) {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(AdaController.class)) {
                    String beanName = toLowerFirstCase(clazz.getSimpleName());
                    Object instance = clazz.newInstance();
                    ioc.put(beanName, instance);
                } else if (clazz.isAnnotationPresent(AdaService.class)) {
                    Object instance = clazz.newInstance();
                    //1.默认ID，首字母小写
                    String beanName = toLowerFirstCase(clazz.getSimpleName());
                    //2.如果重名，自定义beanName
                    AdaService service = clazz.getAnnotation(AdaService.class);
                    if (!"".equals(service.value())) {
                        beanName = service.value();
                    }
                    ioc.put(beanName, instance);

                    //3.类型的全类名
                    for (Class<?> i : clazz.getInterfaces()) {
                        //一个接口有多个实现类
                        if (ioc.containsKey(i.getName())) {
                            throw new Exception("the beanName is exists!!!");
                        }
                        ioc.put(i.getName(), instance);
                    }
                } else {
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //首字母小写
    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    /**
     * 扫描加载类
     */
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
                classNames.add(className);
            }

        }
    }

    /**
     * 加载配置
     */
    private void doLoadConfig(String contextConfigLocation) {
        //从classpath路径读取配置文件到内存中
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
        try {
            contextConfig.load(is);
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
}
