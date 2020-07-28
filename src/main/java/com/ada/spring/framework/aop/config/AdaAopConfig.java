package com.ada.spring.framework.aop.config;

import lombok.Data;

/**
 * @author Ada
 * @ClassName :AdaAopConfig
 * @date 2020/7/27 23:30
 * @Description:
 */
@Data
public class AdaAopConfig {
    private String pointCut;
    private String aspectClass;
    private String aspectBefore;
    private String aspectAfter;
    private String aspectAfterThrow;
    private String aspectAfterThrowingName;
}
