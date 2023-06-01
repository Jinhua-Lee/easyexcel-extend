package com.jinhua.easyexcel.ext.domain.entity.meta;

import com.alibaba.excel.annotation.ExcelProperty;
import com.jinhua.easyexcel.ext.annotation.ColumnGatheredSubType;
import com.jinhua.easyexcel.ext.annotation.DynamicColumnAnalysis;
import com.jinhua.easyexcel.ext.domain.valobj.meta.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 动态列分析类型，元数据
 *
 * @author Jinhua-Lee
 */
@Getter
@EqualsAndHashCode(of = "type")
public class DynamicColumnAnalysisInfo {

    private final Class<?> type;

    /**
     * 第一层对象，class信息 + fields信息 + 注解信息
     */
    private final ParentTypeAndFieldsVO parent;

    public DynamicColumnAnalysisInfo(Class<?> type) {
        this.type = type;
        this.parent = new ParentTypeAndFieldsVO(type);
    }

    public DynamicMetaAndDataToWrite metaAndDataToWrite(Collection<?> dynamicColumnCollection) {
        // 1. 构建Meta信息
        List<List<String>> cellNamesList = new ArrayList<>();
        //      1.1 @ExcelProperty字段列表
        // TreeSet
        Set<FieldAndAnnotationVO> nonGatheredFieldsAndAnnotations = this.parent.getNonGatheredFieldsAndAnnotations();
        nonGatheredFieldsAndAnnotations.forEach(nonGather -> {
            String[] excelPropertyValue = ((ExcelProperty) nonGather.getAnnotation()).value();
            cellNamesList.add(Arrays.asList(excelPropertyValue));
        });
        //      1.2 @CollectionGathered字段列表铺平
        // LinkedHashMap
        LinkedHashMap<FieldAndAnnotationWithGenericType, Integer> maxColFieldNumMap =
                this.parent.maxColFieldNumMap4Entity(dynamicColumnCollection);
        maxColFieldNumMap.forEach((faaWithGeneric, num) -> {
            // 类型注解上的【对象类型】标识
            SubTypeAndFieldsVO subTypeAndFields = faaWithGeneric.getSubTypeAndFields();
            ColumnGatheredSubType dynamicAnalysis = (ColumnGatheredSubType) subTypeAndFields
                    .getTypeAndAnnotation().getAnnotation();
            String subTypeIdentity = dynamicAnalysis.subTypeIdentity();
            char separator = dynamicAnalysis.separator();
            // 字段注解上的【字段】标识
            // 暂定规则是数字递增
            // 对每个对象
            for (AtomicInteger ia = new AtomicInteger(1); ia.get() <= num; ia.getAndIncrement()) {
                int objSerialNum = ia.get();
                Optional.ofNullable(subTypeAndFields.getSubFieldAndAnnotations())
                        .orElse(Collections.emptySet()).forEach(subFieldAndAnnotation -> {
                            String subFieldIdentity = ((DynamicColumnAnalysis) subFieldAndAnnotation.getAnnotation())
                                    .subFieldIdentity();
                            cellNamesList.add(Collections.singletonList(
                                    subTypeIdentity + separator
                                            + objSerialNum + separator
                                            + subFieldIdentity
                            ));
                        });
            }
        });

        return null;
    }

}
