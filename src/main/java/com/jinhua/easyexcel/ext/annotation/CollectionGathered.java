package com.jinhua.easyexcel.ext.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识一个字段，是集合类型，引用的元素是动态组装的子对象
 * 集合类型必须引用其实现类，不能引用于其抽象
 *
 * @author Jinhua-Lee
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface CollectionGathered {
}
