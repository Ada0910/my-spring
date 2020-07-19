package com.ada.spring.framework.webmvc.servlet;

import com.ada.spring.framework.annotation.AdaRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ada
 * @ClassName :AdaHandlerAdapter
 * @date 2020/7/14 23:36
 * @Description:
 */
public class AdaHandlerAdapter {
    public AdaModelAndView handle(HttpServletRequest req, HttpServletResponse resp, AdaHandlerMapping handler) throws Exception {

        //解析形参
        Map<String, Integer> paramIndexMapping = new HashMap<>();
        Annotation[][] pa = handler.getMethod().getParameterAnnotations();
        for (int i = 0; i < pa.length; i++) {
            for (Annotation a : pa[i]) {
                if (a instanceof AdaRequestParam) {
                    String paramName = ((AdaRequestParam) a).value();
                    if (!"".equals(paramName)) {
                        paramIndexMapping.put(paramName, i);

                    }

                }
            }
        }

        //提取request 和response的位置
        Class<?>[] paramTypes = handler.getMethod().getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> type = paramTypes[i];
            if (type == HttpServletRequest.class || type == HttpServletResponse.class) {
                paramIndexMapping.put(type.getName(), i);
            }

        }

        Map<String, String[]> params = req.getParameterMap();


        //形参列表
        Class<?>[] paramterTypes = handler.getMethod().getParameterTypes();
        //实参列表
        Object[] paramtValues = new Object[paramterTypes.length];

        for (Map.Entry<String, String[]> param : params.entrySet()) {
            String value = Arrays.toString(params.get(param.getKey())).replaceAll("\\[|\\]}", "").replaceAll("\\s", "");
            if (!paramIndexMapping.containsKey(param.getKey())) {
                continue;
            }
            int index = paramIndexMapping.get(param.getKey());
            paramtValues[index] = caseStringValue(value, paramterTypes[index]);
        }
        if (paramIndexMapping.containsKey(HttpServletRequest.class.getName())) {
            int index = paramIndexMapping.get(HttpServletRequest.class.getName());
            paramtValues[index] = req;
        }

        if (paramIndexMapping.containsKey(HttpServletResponse.class.getName())) {
            int index = paramIndexMapping.get(HttpServletResponse.class.getName());
            paramtValues[index] = resp;
        }


        Object result = handler.getMethod().invoke(handler.getController(), paramtValues);
        if (result == null || result instanceof Void) {
            return null;
        }
        boolean isModelAndView = (handler.getMethod().getReturnType() == AdaModelAndView.class);
        if (isModelAndView) {
            return (AdaModelAndView) result;
        }
        return null;
    }

    private Object caseStringValue(String value, Class<?> paramterType) {
        if (String.class == paramterType) {
            return value;
        }
        if (Integer.class == paramterType) {
            return Integer.valueOf(value);
        } else if (Double.class == paramterType) {
            return Double.valueOf(value);
        } else {
            if (value != null) {
                return value;
            }
            return null;
        }
    }
}
