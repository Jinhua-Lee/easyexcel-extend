package com.jinhua.easyexcel.ext.domain.service.convertor;

import java.lang.reflect.Field;
import java.time.format.DateTimeParseException;

/**
 * 字段设置器
 * 1. Excel中的Cell目前是以String类型接收的。
 * 2. Java中支持一些基础的数据类型转换
 *
 * @author Jinhua-Lee
 */
public interface CellString2FieldSetter {

    /**
     * 转换为java类型值<p>
     *
     * @param field   类的字段
     * @param cellVal cell中的string类型值
     * @param obj     待设置对象
     * @throws IllegalAccessException   权限不合法异常
     * @throws IllegalArgumentException 参数不合法异常
     * @throws DateTimeParseException   日期时间解析异常
     */
    void cellString2Field(Field field, String cellVal, Object obj)
            throws IllegalAccessException, IllegalArgumentException, DateTimeParseException;
}
