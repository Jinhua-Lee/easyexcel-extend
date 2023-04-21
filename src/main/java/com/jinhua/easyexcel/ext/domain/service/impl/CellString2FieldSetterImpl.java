package com.jinhua.easyexcel.ext.domain.service.impl;

import com.jinhua.easyexcel.ext.domain.service.CellString2FieldSetter;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/**
 * @author Jinhua-Lee
 */
public class CellString2FieldSetterImpl implements CellString2FieldSetter {

    @Override
    public void cellString2Field(Field field, String cellVal, Object obj)
            throws IllegalAccessException, IllegalArgumentException, DateTimeParseException {
        Class<?> type = field.getType();
        if (type == String.class) {
            field.set(obj, cellVal);
        } else if (type == Boolean.TYPE || type == Boolean.class) {
            field.setBoolean(obj, Boolean.parseBoolean(cellVal));
        } else if (type == Byte.TYPE || type == Byte.class) {
            field.setByte(obj, Byte.parseByte(cellVal));
        } else if (type == Short.TYPE || type == Short.class) {
            field.setShort(obj, Short.parseShort(cellVal));
        } else if (type == Integer.TYPE || type == Integer.class) {
            field.setInt(obj, Integer.parseInt(cellVal));
        } else if (type == Long.TYPE || type == Long.class) {
            field.setLong(obj, Long.parseLong(cellVal));
        } else if (type == Double.TYPE || type == Double.class) {
            field.setDouble(obj, Double.parseDouble(cellVal));
        } else if (type == LocalDate.class) {
            field.set(obj, LocalDate.parse(cellVal));
        } else if (type == LocalTime.class) {
            field.set(obj, LocalTime.parse(cellVal));
        } else if (type == LocalDateTime.class) {
            field.set(obj, LocalDateTime.parse(cellVal));
        }
    }
}
