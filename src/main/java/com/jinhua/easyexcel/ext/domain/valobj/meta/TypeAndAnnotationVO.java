package com.jinhua.easyexcel.ext.domain.valobj.meta;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.annotation.Annotation;

/**
 * 类型及其注解
 *
 * @author Jinhua-Lee
 */
@Data
@EqualsAndHashCode(of = "type")
public class TypeAndAnnotationVO {

    private Class<?> type;
    private Annotation dynamicColumnAnalysisAnnotation;

    public TypeAndAnnotationVO(Class<?> type, Class<? extends Annotation> annotationClass) {
        this.type = type;
        this.dynamicColumnAnalysisAnnotation = type.getAnnotation(annotationClass);
    }

    public boolean implTypeOf(Class<?> baseType) {
        return baseType.isAssignableFrom(this.type);
    }
}
