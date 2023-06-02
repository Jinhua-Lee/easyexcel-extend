package com.jinhua.easyexcel.ext.domain.entity.meta;

import com.alibaba.excel.annotation.ExcelProperty;
import com.jinhua.easyexcel.ext.annotation.ColumnGatheredSubType;
import com.jinhua.easyexcel.ext.annotation.DynamicColumnAnalysis;
import com.jinhua.easyexcel.ext.annotation.ObjectIdentityStrategy;
import com.jinhua.easyexcel.ext.domain.entity.IColumnGatheredSubType;
import com.jinhua.easyexcel.ext.domain.valobj.meta.*;
import com.jinhua.easyexcel.ext.domain.valobj.meta.out.DynamicMetaAndDataToWrite;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 动态列分析类型，元数据
 *
 * @author Jinhua-Lee
 */
@Slf4j
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

    @SuppressWarnings(value = "unchecked")
    public DynamicMetaAndDataToWrite metaAndDataToWrite(Collection<?> dynamicColumnCollection) {
        LinkedHashMap<List<String>, FieldAndAnnotationVO> fieldNames2fieldMetaMap = buildMeta(dynamicColumnCollection);

        // 2. 构建Data信息
        // 需要根据Meta信息完成对象的构建
        List<Object[]> rows = new ArrayList<>(dynamicColumnCollection.size());
        // 对每行数据
        dynamicColumnCollection.forEach(dynamicColumnObject -> {
            Object[] row = new Object[fieldNames2fieldMetaMap.size()];

            // 记录excel中的字段顺序，用于匹配meta信息和data信息能在同一列
            AtomicInteger excelFieldIndex = new AtomicInteger(-1);
            // 逐个获取字段并放置，如果没有找到，则设置null
            fieldNames2fieldMetaMap.forEach((fieldName, fieldMeta) -> {
                int fieldIndex = excelFieldIndex.incrementAndGet();
                FieldAndAnnotationVO parent = fieldMeta.getParent();
                // 1. 当作父对象的属性去解析
                if (parent == null) {
                    ExcelProperty excelProperty = (ExcelProperty) fieldMeta.getAnnotation();
                    // value是为了支持多级表头或多个列，这里对象来自同一个地方，所以可以直接比较相等性
                    if (Objects.equals(fieldName, Arrays.asList(excelProperty.value()))) {
                        try {
                            Object parentFiledObj = fieldMeta.getField().get(dynamicColumnObject);
                            row[fieldIndex] = parentFiledObj;
                        } catch (IllegalAccessException e) {
                            log.error("动态对象输出解析，解析父对象，权限访问异常！");
                        }
                    }
                    return;
                }
                // 2. 当作子对象去解析
                // 2.1 先拿到父对象的属性
                Collection<? extends IColumnGatheredSubType> subObjects;
                try {
                    subObjects = (Collection<? extends IColumnGatheredSubType>)
                            parent.getField().get(dynamicColumnObject);
                } catch (IllegalAccessException e) {
                    log.error("动态对象输出解析，解析子对象，权限访问异常！");
                }
            });
        });

        return null;
    }

    private LinkedHashMap<List<String>, FieldAndAnnotationVO> buildMeta(Collection<?> dynamicColumnCollection) {
        // 1. 构建Meta信息：每条记录即是铺平的Excel字段信息
        // 采用LinkedHashMap，严格保证Excel入参要求的添加顺序
        // key有两种类型
        //  - FieldAndAnnotationVO：父对象的非动态解析属性及注解，不带子对象信息
        //  - FieldAndAnnotationVO：子对象的属性，从父对象的动态解析属性来，带父对象的注解信息
        LinkedHashMap<List<String>, FieldAndAnnotationVO> fieldNames2fieldMetaMap = new LinkedHashMap<>();

        //      1.1 @ExcelProperty字段列表
        // TreeSet
        Set<FieldAndAnnotationVO> nonGatheredFieldsAndAnnotations = this.parent.getNonGatheredFieldsAndAnnotations();
        nonGatheredFieldsAndAnnotations.forEach(nonGather -> {
            String[] excelPropertyValue = ((ExcelProperty) nonGather.getAnnotation()).value();
            fieldNames2fieldMetaMap.put(Arrays.asList(excelPropertyValue), nonGather);
        });
        //      1.2 @CollectionGathered字段列表铺平
        // LinkedHashMap
        LinkedHashMap<FieldAndAnnotationWithGenericType, Integer> maxColFieldNumMap =
                this.parent.maxColFieldNumMap4Entity(dynamicColumnCollection);
        maxColFieldNumMap.forEach((faaWithGeneric, num) -> {
            // 类型注解上的【对象类型】标识
            SubTypeAndFieldsVO subTypeAndFields = faaWithGeneric.getSubTypeAndFields();
            ColumnGatheredSubType columnGatheredSubType = (ColumnGatheredSubType) subTypeAndFields
                    .getTypeAndAnnotation().getAnnotation();
            String subTypeIdentity = columnGatheredSubType.subTypeIdentity();
            char separator = columnGatheredSubType.separator();
            ObjectIdentityStrategy objectIdentityStrategy = columnGatheredSubType.objectIdentityStrategy();
            switch (objectIdentityStrategy.value()) {
                case 1:
                    for (AtomicInteger ia = new AtomicInteger(objectIdentityStrategy.autoIncrementStartNum());
                         ia.get() <= num; ia.getAndIncrement()) {
                        int objSerialNum = ia.get();
                        Optional.ofNullable(subTypeAndFields.getSubFieldAndAnnotations())
                                .orElse(Collections.emptySet()).forEach(subFieldAndAnnotation -> {
                                    String subFieldIdentity = ((DynamicColumnAnalysis)
                                            subFieldAndAnnotation.getAnnotation()
                                    ).subFieldIdentity();
                                    fieldNames2fieldMetaMap.put(
                                            Collections.singletonList(
                                                    subTypeIdentity + separator
                                                            + objSerialNum + separator
                                                            + subFieldIdentity
                                            ),
                                            subFieldAndAnnotation
                                    );
                                });
                    }
                    break;
                case 2:
                    for (String identity : objectIdentityStrategy.objectIdentityRange()) {
                        Optional.ofNullable(subTypeAndFields.getSubFieldAndAnnotations())
                                .orElse(Collections.emptySet()).forEach(subFieldAndAnnotation -> {
                                    String subFieldIdentity = ((DynamicColumnAnalysis)
                                            subFieldAndAnnotation.getAnnotation()
                                    ).subFieldIdentity();
                                    fieldNames2fieldMetaMap.put(
                                            Collections.singletonList(
                                                    subTypeIdentity + separator
                                                            + identity + separator
                                                            + subFieldIdentity
                                            ),
                                            subFieldAndAnnotation
                                    );
                                });
                    }
                    break;
                default:
                    throw new IllegalStateException("unsupported object identity strategy");
            }
        });
        return fieldNames2fieldMetaMap;
    }

}
