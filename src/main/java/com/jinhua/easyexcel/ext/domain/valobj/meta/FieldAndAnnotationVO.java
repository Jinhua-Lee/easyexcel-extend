package com.jinhua.easyexcel.ext.domain.valobj.meta;

import com.jinhua.easyexcel.ext.domain.service.CellString2FieldSetter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author Jinhua-Lee
 */
@Slf4j
@Getter
@EqualsAndHashCode(of = "field")
@ToString
public class FieldAndAnnotationVO {
    private final Field field;
    private final Annotation annotation;

    public FieldAndAnnotationVO(Field field, Annotation annotation) {
        if (field == null) {
            throw new IllegalArgumentException("field must not be null.");
        }
        this.field = field;
        if (annotation == null) {
            throw new IllegalArgumentException("annotation must not be null.");
        }
        this.annotation = annotation;
    }

    public void setCellStringField4Entity(Object entity, String value, CellString2FieldSetter cellString2FieldSetter) {
        try {
            cellString2FieldSetter.cellString2Field(this.field, value, entity);
        } catch (IllegalAccessException e) {
            log.error("访问权限错误，message = {}", e.getMessage());
        }
    }

}
