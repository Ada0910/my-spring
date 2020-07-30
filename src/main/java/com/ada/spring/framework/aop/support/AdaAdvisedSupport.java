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
                if (matcher.matches()) {
                    Map<String, AdaAdvice> advices = new HashMap<>();
                    //前置通知
                    if (!(null == config.getAspectBefore() || "".equals(config.getAspectBefore()))) {
                        advices.put("before", new AdaAdvice(aspectClass.newInstance(), aspectMethods.get(config.getAspectBefore())));
                    }
                    // 后置通知
                    if (!(null == config.getAspectAfter() || "".equals(config.getAspectAfter()))) {
                        advices.put("after", new AdaAdvice(aspectClass.newInstance(), aspectMethods.get(config.getAspectAfter())));
                    }
                    //异常通知
                    if (!(null == config.getAspectAfterThrow() || "".equals(config.getAspectAfterThrow()))) {
                        advices.put("afterThrow", new AdaAdvice(aspectClass.newInstance(), aspectMethods.get(config.getAspectAfterThrow())));
                    }
                    methodCache.put(method, advices);
                }

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

    public Map<String, AdaAdvice> getAdvice(Method method, Class targetClass) throws Exception {
        Map<String, AdaAdvice> cache = methodCache.get(method);
        if (null == cache) {
            Method m = targetClass.getMethod(method.getName(), method.getParameterTypes());
            cache = methodCache.get(m);
            this.methodCache.put(m, cache);
        }
        return cache;
    }
}
