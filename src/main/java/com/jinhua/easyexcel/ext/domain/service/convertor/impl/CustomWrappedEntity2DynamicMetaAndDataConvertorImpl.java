package com.jinhua.easyexcel.ext.domain.service.convertor.impl;

import com.jinhua.easyexcel.ext.annotation.DynamicColumnAnalysis;
import com.jinhua.easyexcel.ext.domain.entity.meta.DynamicColumnAnalysisInfo;
import com.jinhua.easyexcel.ext.domain.service.convertor.CustomWrappedEntity2DynamicMetaAndDataConvertor;
import com.jinhua.easyexcel.ext.domain.valobj.meta.DynamicMetaAndDataToWrite;
import com.jinhua.easyexcel.ext.domain.valobj.meta.FieldAndAnnotationWithGenericType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jinhua-Lee
 */
@Slf4j
@Service
public class CustomWrappedEntity2DynamicMetaAndDataConvertorImpl
        implements CustomWrappedEntity2DynamicMetaAndDataConvertor {

    private final Map<Class<?>, DynamicColumnAnalysisInfo> dynamicColumnAnalysisInfos = new ConcurrentHashMap<>();

    @Override
    public <T> DynamicMetaAndDataToWrite convert(Collection<T> entities, Class<T> tClass) {
        if (ObjectUtils.isEmpty(entities)) {
            throw new IllegalArgumentException("Entities must not be empty!");
        }
        // 1. 校验类型注解
        if (!tClass.isAnnotationPresent(DynamicColumnAnalysis.class)) {
            throw new IllegalStateException(
                    String.format("type = %s is not suitable for dynamic column analysis." +
                            " Make sure the Type is annotated with @DynamicColumnAnalysis.", tClass)
            );
        }

        // 2. 找出
        DynamicColumnAnalysisInfo analysisInfo = dynamicColumnAnalysisInfos.compute(tClass,
                (clazz, aInfo) -> {
                    if (aInfo == null) {
                        aInfo = new DynamicColumnAnalysisInfo(tClass);
                    }
                    return aInfo;
                });

        // 遍历各个对象中的最大值，用于统计Meta个数
        Map<FieldAndAnnotationWithGenericType, Integer> maxColFieldNumMap =
                analysisInfo.getParent().maxColFieldNumMap4Entity(entities);

        // 3. 构建Meta信息
        //      2.1 @ExcelProperty字段列表
        //      2.2 @CollectionGathered字段列表铺平


        return null;
    }
}
