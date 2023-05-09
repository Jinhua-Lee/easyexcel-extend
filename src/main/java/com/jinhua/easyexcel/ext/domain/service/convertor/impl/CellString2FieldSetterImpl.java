package com.jinhua.easyexcel.ext.domain.service.convertor.impl;

import com.jinhua.easyexcel.ext.domain.service.convertor.CellString2FieldSetter;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/**
 * @author Jinhua-Lee
 */
@Component
@SuppressWarnings("unused")
public class CellString2FieldSetterImpl implements CellString2FieldSetter {

    @Override
    public void cellString2Field(Field field, String cellVal, Object obj)
            throws IllegalAccessException, IllegalArgumentException, DateTimeParseException {
        if (ObjectUtils.isEmpty(cellVal)) {
            return;
        }
        Class<?> type = field.getType();
        if (type == String.class) {
            field.set(obj, cellVal);
        } else if (type.isPrimitive()) {
            set4Primitive(field, cellVal, obj, type);
        }
        // wrapped type
        else if (type == Boolean.class) {
            field.set(obj, Boolean.parseBoolean(cellVal));
        } else if (type == Byte.class) {
            field.set(obj, Byte.parseByte(cellVal));
        } else if (type == Short.class) {
            field.set(obj, Short.parseShort(cellVal));
        } else if (type == Integer.class) {
            field.set(obj, Integer.parseInt(cellVal));
        } else if (type == Float.class) {
            field.set(obj, Float.parseFloat(cellVal));
        } else if (type == Double.class) {
            field.set(obj, Double.parseDouble(cellVal));
        } else if (type == Long.class) {
            field.set(obj, Long.parseLong(cellVal));
        }
        // date type
        else if (type == LocalDate.class) {
            field.set(obj, LocalDate.parse(cellVal));
        } else if (type == LocalTime.class) {
            field.set(obj, LocalTime.parse(cellVal));
        } else if (type == LocalDateTime.class) {
            field.set(obj, LocalDateTime.parse(cellVal));
        }
        // decimal type
        else if (type == BigDecimal.class) {
            field.set(obj, new BigDecimal(cellVal));
        }
    }

    private void set4Primitive(Field field, String cellVal, Object obj, Class<?> type) throws IllegalAccessException {
        // primitive type
        if (type == Boolean.TYPE) {
            field.setBoolean(obj, Boolean.parseBoolean(cellVal));
        } else if (type == Byte.TYPE) {
            field.setByte(obj, Byte.parseByte(cellVal));
        } else if (type == Short.TYPE) {
            field.setShort(obj, Short.parseShort(cellVal));
        } else if (type == Integer.TYPE) {
            field.setInt(obj, Integer.parseInt(cellVal));
        } else if (type == Float.TYPE) {
            field.setFloat(obj, Float.parseFloat(cellVal));
        } else if (type == Double.TYPE) {
            field.setDouble(obj, Double.parseDouble(cellVal));
        } else if (type == Long.TYPE) {
            field.setLong(obj, Long.parseLong(cellVal));
        }
    }
}
