package com.cet.matterhorn.easyexcel.ext.domain.valobj.meta;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Optional;

/**
 * 带上集合的泛型类型
 *
 * @author Jinhua-Lee
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class FieldAndAnnotationWithGenericType extends FieldAndAnnotationVO {

    private final SubTypeAndFieldsVO subTypeAndFields;

    public FieldAndAnnotationWithGenericType(Field field, Annotation annotation,
                                             Class<?> subType) {
        super(field, annotation, null);
        // 构造子类型的对象
        this.subTypeAndFields = new SubTypeAndFieldsVO(subType, this);
    }

    public Optional<? extends FieldAndAnnotationVO> matchedSubFieldAndAnnotation(String cellFieldName) {
        return this.subTypeAndFields.matchedSubFieldAndAnnotation(cellFieldName);
    }
}