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
public @interface ObjectIdentityStrategy {

    /**
     * 策略：
     * 1. 给定序号自增
     * 2. 给定范围枚举
     *
     * @return 策略枚举值
     */
    int value() default 1;

    /**
     * 为自增策略时，起始值
     *
     * @return 自增起始值
     */
    int autoIncrementStartNum() default 1;

    /**
     * 【给定范围枚举】策略时，范围枚举值<p>
     * 不允许是纯数字
     *
     * @return 【给定范围枚举】
     */
    String[] objectIdentityRange() default {};
}