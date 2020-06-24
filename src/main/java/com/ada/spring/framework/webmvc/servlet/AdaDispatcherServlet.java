package com.ada.spring.framework.webmvc.servlet;


import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
    }

    private void doScanner(String scanPackage) {
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
