package com.ada.spring.framework.webmvc.servlet;


import com.ada.spring.framework.annotation.AdaRequestMapping;
import com.ada.spring.framework.annotation.AdaRequestParam;
import com.ada.spring.framework.context.AdaApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Ada
 * @ClassName :AdaDispatcherServlet
 * @date 2020/6/22 23:42
 * @Description:
 */
public class AdaDispatcherServlet extends HttpServlet {

    private AdaApplicationContext applicationContext;

    private List<String> classNames = new ArrayList<>();

    private Map<String, Method> handlerMapping = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //7.调用
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("500");
        }

    }

    /**
     * 7.调用
     */
    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        //相对路径
        url = url.replaceAll(contextPath, "").replaceAll("/+", "/");
        if (!this.handlerMapping.containsKey(url)) {
            resp.getWriter().write("404 Not Found!!!");
            return;
        }
        Method method = this.handlerMapping.get(url);
        Map<String, String[]> params = req.getParameterMap();


        //形参列表
        Class<?>[] paramterTypes = method.getParameterTypes();
        //实参列表
        Object[] paramtValues = new Object[paramterTypes.length];

        for (int i = 0; i < paramterTypes.length; i++) {
            Class paramterType = paramterTypes[i];
            if (paramterType == HttpServletRequest.class) {
                paramtValues[i] = req;
            } else if (paramterType == HttpServletResponse.class) {
                paramtValues[i] = resp;
            } else if (paramterType == String.class) {
                Annotation[][] pa = method.getParameterAnnotations();
                for (Annotation a : pa[i]) {
                    if (a instanceof AdaRequestParam) {
                        String paramName = ((AdaRequestParam) a).value();
                        if (!"".equals(paramName)) {
                            String value = Arrays.toString(params.get(paramName)).replaceAll("\\[|\\]}", "").replaceAll("\\s", "");
                            paramtValues[i] = value;
                        }

                    }
                }
            }

        }

        method.invoke(applicationContext.getBean(method.getDeclaringClass()), paramtValues);

    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        applicationContext = new AdaApplicationContext(config.getInitParameter("contextConfigLocation"));

        //===============3.MVC======================================
        //6.初始化handlerMapping
        doHandlerMapping();

        //////////////////初始化阶段完成/////////////////
        System.out.println(">>>>>>>>>>>>>>>My Spring framework is init .........>>>>>>>>>>>>>>>");
    }

    /**
     * 6.初始化handlerMapping
     */
    private void doHandlerMapping() {

        if (applicationContext.getBeanDefinitionCount() == 0) {
            return;
        }

        String[] beanNames = applicationContext.getBeanDefinitionName();

        for (String beanName : beanNames) {
            Object instance = applicationContext.getBean(beanName);
            Class<?> clazz = instance.getClass();
            String baseUrl = "";
            if (clazz.isAnnotationPresent(AdaRequestMapping.class)) {
                AdaRequestMapping requestMapping = clazz.getAnnotation(AdaRequestMapping.class);
                baseUrl = requestMapping.value();
            }
            for (Method method : clazz.getMethods()) {
                if (!method.isAnnotationPresent(AdaRequestMapping.class)) {
                    continue;
                }
                AdaRequestMapping requestMapping = method.getAnnotation(AdaRequestMapping.class);
                String url = ("/" + baseUrl + "/" + requestMapping.value()).replaceAll("/+", "/");
                handlerMapping.put(url, method);
                System.out.println("Mapper:" + url + "," + method);
            }
        }
    }

}
