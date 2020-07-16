package com.ada.spring.framework.webmvc.servlet;

import com.ada.spring.framework.annotation.AdaRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

/**
 * @author Ada
 * @ClassName :AdaHandlerAdapter
 * @date 2020/7/14 23:36
 * @Description:
 */
public class AdaHandlerAdapter {
    public AdaModelAndView handle(HttpServletRequest req, HttpServletResponse resp, AdaHandlerMapping handler) throws Exception {

        Method method = handler.getMethod();
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

        Object result = method.invoke(handler.getController(), paramtValues);
        if (result == null || result instanceof Void) {
            return null;
        }
        boolean isModelAndView = (method.getReturnType() == AdaModelAndView.class);
        if (isModelAndView) {
            return (AdaModelAndView) result;
        }
        return null;
    }
}
