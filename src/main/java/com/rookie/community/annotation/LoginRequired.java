package com.rookie.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//注解范围：方法方法上
@Target(ElementType.METHOD)
//启动时间：运行时
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {

}
