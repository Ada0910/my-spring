package com.ada.spring.framework.webmvc.servlet;

import java.util.Map;

/**
 * @author Ada
 * @ClassName :AdaModelAndView
 * @date 2020/7/14 23:37
 * @Description:
 */
public class AdaModelAndView {

    private String viewName;
    private Map<String, ?> model;

    public AdaModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public AdaModelAndView(String viewName, Map<String, Object> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }
}
