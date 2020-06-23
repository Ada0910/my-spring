package com.ada.demo.service.impl;


import com.ada.spring.framework.annotation.AdaService;

/***
 * @Author Ada
 * @Date 23:34 2020/6/23
 * @Param
 * @return
 * @Description
 **/

@AdaService
public class ModifyServiceImpl implements com.ada.demo.service.ModifyService {

    /**
     * 增加
     */
    @Override
    public String add(String name, String addr) {
        return "modifyService add,name=" + name + ",addr=" + addr;
    }

    /**
     * 修改
     */
    @Override
    public String edit(Integer id, String name) {
        return "modifyService edit,id=" + id + ",name=" + name;
    }

    /**
     * 删除
     */
    @Override
    public String remove(Integer id) {
        return "modifyService id=" + id;
    }

}
