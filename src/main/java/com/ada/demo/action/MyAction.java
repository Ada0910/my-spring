package com.ada.demo.action;

import com.ada.demo.service.ModifyService;
import com.ada.demo.service.QueryService;
import com.ada.spring.framework.annotation.AdaAutowired;
import com.ada.spring.framework.annotation.AdaController;
import com.ada.spring.framework.annotation.AdaRequestMapping;
import com.ada.spring.framework.annotation.AdaRequestParam;
import com.ada.spring.framework.webmvc.servlet.AdaModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Ada
 * @ClassName :MyAction
 * @date 2020/6/23 23:16
 * @Description:
 */
@AdaController
@AdaRequestMapping("/web")
public class MyAction {
    @AdaAutowired
    QueryService queryService;

    @AdaAutowired
    ModifyService modifyService;

    @AdaRequestMapping("/query")
    public AdaModelAndView query(HttpServletRequest request, HttpServletResponse response,
                                 @AdaRequestParam("name") String name, @AdaRequestParam("id") String id) {
        String result = queryService.query(name);
        System.out.println(id);
        return out(response, result);
    }

    @AdaRequestMapping("/add*.json")
    public AdaModelAndView add(HttpServletRequest request, HttpServletResponse response,
                               @AdaRequestParam("name") String name, @AdaRequestParam("addr") String addr) {
        String result = modifyService.add(name, addr);
        return out(response, result);
    }

    @AdaRequestMapping("/remove.json")
    public AdaModelAndView remove(HttpServletRequest request, HttpServletResponse response,
                                  @AdaRequestParam("id") Integer id) {
        String result = modifyService.remove(id);
        return out(response, result);
    }

    @AdaRequestMapping("/edit.json")
    public AdaModelAndView edit(HttpServletRequest request, HttpServletResponse response,
                                @AdaRequestParam("id") Integer id,
                                @AdaRequestParam("name") String name) {
        String result = modifyService.edit(id, name);
        return out(response, result);
    }


    private AdaModelAndView out(HttpServletResponse resp, String str) {
        try {
            resp.getWriter().write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
