package com.ada.spring.framework.aop.support;

import com.ada.spring.framework.aop.aspect.AdaAdvice;
import com.ada.spring.framework.aop.config.AdaAopConfig;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ada
 * @ClassName :AdaAdvisedSupport
 * @date 2020/7/27 23:29
 * @Description:
 */
public class AdaAdvisedSupport {
    private AdaAopConfig config;
    private Class targetClass;
    private Object target;
    private Pattern pointCutClassPattern;
    private Map<Method, Map<String, AdaAdvice>> methodCache;

    //解析读取出来的配置信息
    public AdaAdvisedSupport(AdaAopConfig config) {
        this.config = config;
    }


    public Class getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    private void parse() {
        String pointCut = config.getPointCut()
                .replaceAll("\\.", "\\\\.")
                .replaceAll("\\\\.\\*", ".*")
                .replaceAll("\\(", "\\\\(")
                .replaceAll("\\)", "\\\\)");

        String pointCutForClassRegex = pointCut.substring(0, pointCut.lastIndexOf("\\(") - 4);
        //提取class的全名
        pointCutClassPattern = Pattern.compile("Class " +
                pointCutForClassRegex.substring(pointCutForClassRegex.lastIndexOf(" " + 1)));
        try {
            //保存方法和通知的关系
            methodCache = new HashMap<>();
            Pattern pointCutPattern = Pattern.compile(pointCut);
            Class aspectClass = Class.forName(this.config.getAspectClass());
            Map<String, Method> aspectMethods = new HashMap<>();
            for (Method method : aspectClass.getMethods()) {
                aspectMethods.put(method.getName(), method);

            }
            for (Method method : this.targetClass.getMethods()) {
                String methodString = method.toString();
                if (methodString.contains("throws")) {
                    methodString = methodString.substring(0, methodString.lastIndexOf("throws")).trim();
                }
                Matcher matcher = pointCutPattern.matcher(methodString);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean pointCutMatch() {
        return pointCutClassPattern.matcher(this.targetClass.getName()).matches();
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Map<String, AdaAdvice> getAdvice(Method method, Class targetClass) {
        return null;
    }
}
