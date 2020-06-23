package com.ada.demo.service.impl;

import com.ada.spring.framework.annotation.AdaService;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

/***
 * @Author Ada
 * @Date 23:33 2020/6/23
 * @Param
 * @return
 * @Description
 **/
@AdaService
@Slf4j
public class QueryServiceImpl implements com.ada.demo.service.QueryService {

    /**
     * 查询
     */
    @Override
    public String query(String name) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());
        String json = "{name:\"" + name + "\",time:\"" + time + "\"}";
        log.info("这是在业务方法中打印的：" + json);
        return json;
    }

}
