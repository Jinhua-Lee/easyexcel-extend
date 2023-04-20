package com.jinhua.easyexcel.ext.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Jinhua-Lee
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface ColumnGatheredSubType {

    /**
     * excel字段名中的类型标识
     *
     * @return excel字段名中的类型标识
     */
    String subTypeIdentity();

    /**
     * 字段名分隔符
     *
     * @return 字段名分隔符
     */
    char separator();
}
