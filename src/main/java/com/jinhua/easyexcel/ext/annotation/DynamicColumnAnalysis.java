package com.jinhua.easyexcel.ext.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 动态列前缀的注解
 *
 * @author Jinhua-Lee
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD, ElementType.TYPE})
public @interface DynamicColumnAnalysis {

    /**
     * 作用于方法时动态列的前缀，用于标识多列属于同一个对象
     *
     * @return 动态列前缀
     */
    String subFieldIdentity() default "";
}
