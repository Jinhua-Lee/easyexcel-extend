package com.jinhua.easyexcel.ext.domain.valobj.meta;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * @author Jinhua-Lee
 */
@Getter
@EqualsAndHashCode(of = "typeAndAnnotation")
@ToString
public class BaseTypeAndFieldsVO {

    /**
     * 类型及注解
     */
    protected final TypeAndAnnotationVO typeAndAnnotation;

    /**
     * 非上述两种注解的属性信息
     */
    @SuppressWarnings("unused")
    protected Set<FieldAndAnnotationVO> otherFields;

    public BaseTypeAndFieldsVO(Class<?> type,  Class<? extends Annotation> annotationClass) {
        this.typeAndAnnotation = new TypeAndAnnotationVO(type, annotationClass);
    }

}
