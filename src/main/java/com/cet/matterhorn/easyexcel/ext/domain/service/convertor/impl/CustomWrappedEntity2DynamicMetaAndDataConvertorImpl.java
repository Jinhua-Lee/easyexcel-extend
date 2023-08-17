package com.cet.matterhorn.easyexcel.ext.domain.service.convertor.impl;

import com.cet.matterhorn.easyexcel.ext.domain.entity.meta.DynamicColumnAnalysisInfo;
import com.cet.matterhorn.easyexcel.ext.domain.service.convertor.CustomWrappedEntity2DynamicMetaAndDataConvertor;
import com.cet.matterhorn.easyexcel.ext.domain.valobj.meta.out.DynamicMetaAndDataToWrite;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.Map;
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
    public <T> DynamicMetaAndDataToWrite convert(Collection<T> entities, Class<T> tClass,
                                                 @Nullable Integer autoIncrementNumIfNull) {
        if (ObjectUtils.isEmpty(entities)) {
            throw new IllegalArgumentException("Entities must not be empty!");
        }
        if (tClass == null) {
            throw new IllegalArgumentException("entity class must not be null!");
        }

        // 1. 构建动态解析对象
        DynamicColumnAnalysisInfo analysisInfo = dynamicColumnAnalysisInfos.compute(tClass,
                (clazz, aInfo) -> {
                    if (aInfo == null) {
                        aInfo = new DynamicColumnAnalysisInfo(tClass);
                    }
                    return aInfo;
                });
        // 2. 解析动态列名及转换数据
        return analysisInfo.metaAndDataToWrite(entities, autoIncrementNumIfNull);
    }
}
