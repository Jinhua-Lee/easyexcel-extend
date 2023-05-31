package com.jinhua.easyexcel.ext.domain.service.convertor;

import com.jinhua.easyexcel.ext.domain.valobj.meta.DynamicMetaAndDataToWrite;

import java.util.Collection;

/**
 * @author Jinhua-Lee
 */
public interface CustomWrappedEntity2DynamicMetaAndDataConvertor {


    /**
     * 通过动态对象，输出动态
     *
     * @param <T>      动态对象类型
     * @param entities 动态对象实体，非empty
     * @param tClass   实体的实现类型
     * @return 动态对象【元信息】【数据】
     */
    <T> DynamicMetaAndDataToWrite convert(Collection<T> entities, Class<T> tClass);
}
