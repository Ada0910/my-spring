package com.ada.demo.action;

import com.ada.demo.service.ModifyService;
import com.ada.demo.service.QueryService;
import com.ada.spring.framework.annotation.AdaAutowired;
import com.ada.spring.framework.annotation.AdaController;
import com.ada.spring.framework.annotation.AdaRequestMapping;
import com.ada.spring.framework.annotation.AdaRequestParam;

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
    public void query(HttpServletRequest request, HttpServletResponse response,
                      @AdaRequestParam("name") String name,@AdaRequestParam("id") String id) {
        String result = queryService.query(name);
        System.out.println(id);
        out(response, result);
    }

    @AdaRequestMapping("/add*.json")
    public void add(HttpServletRequest request, HttpServletResponse response,
                    @AdaRequestParam("name") String name, @AdaRequestParam("addr") String addr) {
        String result = modifyService.add(name, addr);
        out(response, result);
    }

    @AdaRequestMapping("/remove.json")
    public void remove(HttpServletRequest request, HttpServletResponse response,
                       @AdaRequestParam("id") Integer id) {
        String result = modifyService.remove(id);
        out(response, result);
    }

    @AdaRequestMapping("/edit.json")
    public void edit(HttpServletRequest request, HttpServletResponse response,
                     @AdaRequestParam("id") Integer id,
                     @AdaRequestParam("name") String name) {
        String result = modifyService.edit(id, name);
        out(response, result);
    }


    private void out(HttpServletResponse resp, String str) {
        try {
            resp.getWriter().write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
