package com.cet.matterhorn.easyexcel.ext.domain.valobj.meta;

import com.alibaba.excel.annotation.ExcelProperty;
import com.cet.matterhorn.easyexcel.ext.annotation.DynamicColumnAnalysis;
import com.cet.matterhorn.easyexcel.ext.domain.service.convertor.CellString2FieldSetter;
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

    /**
     * 如果是子类型的字段，则需要缓存父类属性信息
     */
    private final FieldAndAnnotationVO parent;

    public FieldAndAnnotationVO(Field field, Annotation annotation, FieldAndAnnotationVO parent) {
        if (field == null) {
            throw new IllegalArgumentException("field must not be null.");
        }
        this.field = field;
        if (annotation == null) {
            throw new IllegalArgumentException("annotation must not be null.");
        }
        this.annotation = annotation;
        this.parent = parent;
    }

    public void setCellStringField4Entity(Object entity, String value, CellString2FieldSetter cellString2FieldSetter) {
        try {
            cellString2FieldSetter.cellString2Field(this.field, value, entity);
        } catch (IllegalAccessException e) {
            log.error("访问权限错误，message = {}", e.getMessage());
        } catch (NumberFormatException ne) {
            ExcelProperty excelProperty = this.field.getAnnotation(ExcelProperty.class);
            DynamicColumnAnalysis dynamicColumn = this.field.getAnnotation(DynamicColumnAnalysis.class);

            throw new IllegalArgumentException(
                    String.format("字段转换错误，字段 = %s，字段类型 = %s， 填入值 = %s ，message = %s",
                            excelProperty == null
                                    ? dynamicColumn.subFieldIdentity()
                                    : String.join(",", excelProperty.value()),
                            this.field.getType().getSimpleName(),
                            value,
                            ne.getMessage()
                    )
            );
        }
    }

}
