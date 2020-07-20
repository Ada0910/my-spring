package com.ada.demo.action;

import com.ada.demo.service.IQueryService;
import com.ada.spring.framework.annotation.AdaAutowired;
import com.ada.spring.framework.annotation.AdaController;
import com.ada.spring.framework.annotation.AdaRequestMapping;
import com.ada.spring.framework.annotation.AdaRequestParam;
import com.ada.spring.framework.webmvc.servlet.AdaModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * 公布接口url
 *
 * @author Tom
 */
@AdaController
@AdaRequestMapping("/")
public class PageAction {

    @AdaAutowired
    IQueryService queryService;

    @AdaRequestMapping("/first.html")
    public AdaModelAndView query(@AdaRequestParam("teacher") String teacher) {
        String result = queryService.query(teacher);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("teacher", teacher);
        model.put("data", result);
        model.put("token", "123456");
        return new AdaModelAndView("first.html", model);
    }

}
