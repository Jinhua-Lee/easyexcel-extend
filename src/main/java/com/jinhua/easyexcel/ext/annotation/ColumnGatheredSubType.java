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

    /**
     * 对象序号策略，当前仅支持
     * 1. 给定序号自增
     * 2. 枚举范围
     *
     * @return 对象序号策略
     */
    ObjectIdentityStrategy objectIdentityStrategy();
}
