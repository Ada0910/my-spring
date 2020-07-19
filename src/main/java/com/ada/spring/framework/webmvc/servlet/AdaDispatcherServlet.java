package com.ada.spring.framework.webmvc.servlet;


import com.ada.spring.framework.annotation.AdaRequestMapping;
import com.ada.spring.framework.context.AdaApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ada
 * @ClassName :AdaDispatcherServlet
 * @date 2020/6/22 23:42
 * @Description:
 */
public class AdaDispatcherServlet extends HttpServlet {

    private AdaApplicationContext applicationContext;

    private List<String> classNames = new ArrayList<>();

    private List<AdaHandlerMapping> handlerMappings = new ArrayList<>();

    private Map<AdaHandlerMapping, AdaHandlerAdapter> handlerAdapters = new HashMap<>();

    private List<AdaViewResolver> viewResolvers = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //7.调用
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            Map<String, Object> model = new HashMap<>();
            model.put("detail", "500");
            model.put("stackTrace", Arrays.toString(e.getStackTrace()));

            try {
                processDispatchResult(req, resp, new AdaModelAndView("500", model));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //1.根据url去拿到一个handlerMapping
        AdaHandlerMapping handler = getHandler(req);
        if (handler == null) {
            processDispatchResult(req, resp, new AdaModelAndView("404"));
            return;
        }
        //2.根据HandlerMapping 获得一个handlerAdapter

        AdaHandlerAdapter ha = getHandlerAdapter(handler);

        //3.根据handlerAdapter拿到一个ModelAndView
        AdaModelAndView mv = ha.handle(req, resp, handler);

        //4.根据modelAndView决定选择那个viewRoslver渲染
        processDispatchResult(req, resp, mv);
    }

    private AdaHandlerAdapter getHandlerAdapter(AdaHandlerMapping handler) {
        if (this.handlerAdapters.isEmpty()) {
            return null;
        }
        return this.handlerAdapters.get(handler);
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, AdaModelAndView mv) throws Exception {
        if (null == mv) {
            return;
        }

        if (this.viewResolvers.isEmpty()) {
            return;
        }

        for (AdaViewResolver viewResolver : this.viewResolvers) {
            AdaView view = viewResolver.resolverViewName(mv.getViewName());
            view.render(mv.getModel(), req, resp);
        }

    }

    private AdaHandlerMapping getHandler(HttpServletRequest req) {
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        //相对路径
        url = url.replaceAll(contextPath, "").replaceAll("/+", "/");

        for (AdaHandlerMapping mapping : this.handlerMappings) {
            Matcher matcher = mapping.getPattern().matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return mapping;
        }
        return null;
    }


    @Override
    public void init(ServletConfig config) throws ServletException {

        //IoC,DI
        applicationContext = new AdaApplicationContext(config.getInitParameter("contextConfigLocation"));

        //===============3.MVC======================================
        //6.初始化handlerMapping
        initStrategies(applicationContext);

        //////////////////初始化阶段完成/////////////////
        System.out.println(">>>>>>>>>>>>>>>My Spring framework is init .........>>>>>>>>>>>>>>>");
    }

    private void initStrategies(AdaApplicationContext context) {
        initHandlerMappings(context);
        initHandlerAdapters(context);
        initViewResolvers(context);
    }

    private void initViewResolvers(AdaApplicationContext context) {
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getPath();
        File templateRootDir = new File(templateRootPath);
        for (File file : templateRootDir.listFiles()) {
            this.viewResolvers.add(new AdaViewResolver(templateRoot));
        }
    }

    private void initHandlerAdapters(AdaApplicationContext context) {
        for (AdaHandlerMapping mapping : handlerMappings) {
            this.handlerAdapters.put(mapping, new AdaHandlerAdapter());
        }
    }

    private void initHandlerMappings(AdaApplicationContext context) {
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
                String regex = ("/" + baseUrl + "/" + requestMapping.value())
                        .replaceAll("\\*", ".*")
                        .replaceAll("/+", "/");
                //handlerMapping.put(url, method);
                Pattern pattern = Pattern.compile(regex);
                handlerMappings.add(new AdaHandlerMapping(pattern, instance, method));
                System.out.println("Mapper:" + regex + "," + method);
            }
        }
    }


}
