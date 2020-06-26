package com.ada.spring.framework.webmvc.servlet;


import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

    private void doHandlerMapping() {
    }

    private void doAutowired() {
    }

    private void doInstance() {
        //判断实例是否为空
        if (classNames.isEmpty()) {
            return;
        }
        try {
            for (String className : classNames) {
                Class clazz = Class.forName(className);
                Object instance = clazz.newInstance();


                ioc.put("", instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                classNames.add(className);

            }

        }
    }

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
