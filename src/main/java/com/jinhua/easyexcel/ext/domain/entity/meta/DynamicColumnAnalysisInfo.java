package com.jinhua.easyexcel.ext.domain.entity.meta;

import com.alibaba.excel.annotation.ExcelProperty;
import com.jinhua.easyexcel.ext.annotation.ColumnGatheredSubType;
import com.jinhua.easyexcel.ext.annotation.DynamicColumnAnalysis;
import com.jinhua.easyexcel.ext.annotation.ObjectIdentityStrategy;
import com.jinhua.easyexcel.ext.domain.entity.IColumnGatheredSubType;
import com.jinhua.easyexcel.ext.domain.valobj.meta.FieldAndAnnotationVO;
import com.jinhua.easyexcel.ext.domain.valobj.meta.FieldAndAnnotationWithGenericType;
import com.jinhua.easyexcel.ext.domain.valobj.meta.ParentTypeAndFieldsVO;
import com.jinhua.easyexcel.ext.domain.valobj.meta.SubTypeAndFieldsVO;
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
        LinkedHashMap<List<String>, FieldAndAnnotationVO> fieldNames2fieldMeta = buildMeta(dynamicColumnCollection);

        // 2. 构建Data信息
        // 需要根据Meta信息完成对象的构建
        List<Object[]> rows = new ArrayList<>(dynamicColumnCollection.size());
        // 对每行数据
        dynamicColumnCollection.forEach(dynamicColumnObject -> {
            Object[] row = new Object[fieldNames2fieldMeta.size()];
            rows.add(row);

            // 记录excel中的字段顺序，用于匹配meta信息和data信息能在同一列
            AtomicInteger excelFieldIndex = new AtomicInteger(-1);
            // 逐个获取字段并放置，如果没有找到，则设置null
            // 优先遍历的是value(fieldMeta)，并非key(fieldNames)
            fieldNames2fieldMeta.forEach((fieldNames, fieldMeta) -> {
                int fieldIndex = excelFieldIndex.incrementAndGet();
                FieldAndAnnotationVO parent = fieldMeta.getParent();
                // 1. 当作父对象的属性去解析
                if (parent == null) {
                    fillNonGatheredFields(dynamicColumnObject, row, fieldNames, fieldMeta, fieldIndex);
                } else {
                    // 2. 当作子对象去解析
                    //  2.1 先拿到父对象的属性
                    Collection<? extends IColumnGatheredSubType> subObjects;
                    try {
                        subObjects = (Collection<? extends IColumnGatheredSubType>)
                                parent.getField().get(dynamicColumnObject);
                        //  2.2 根据匹配规则去找到同属的字段列表并一起设置
                        ColumnGatheredSubType gatheredSubType = fieldMeta.getField().getDeclaringClass()
                                .getAnnotation(ColumnGatheredSubType.class);
                        ObjectIdentityStrategy identityStrategy = gatheredSubType.objectIdentityStrategy();

                        switch (identityStrategy.value()) {
                            case ObjectIdentityStrategy.STRATEGY_INCREMENT:
                                fillExcelColumn4SubObjByIdentityIncrement(fieldNames2fieldMeta, row,
                                        fieldNames, parent, subObjects, gatheredSubType
                                );
                                break;
                            case ObjectIdentityStrategy.STRATEGY_ENUM_RANGE:
                                fillExcelColumn4SubObjByIdentityEnum(fieldNames2fieldMeta, row,
                                        fieldNames, parent, subObjects, gatheredSubType
                                );
                                break;
                            default:
                                throw new IllegalStateException("unsupported object identity strategy");
                        }

                    } catch (IllegalAccessException e) {
                        log.error("动态对象输出解析，解析子对象，权限访问异常！");
                    }
                }
            });
        });

        return null;
    }

    private void fillExcelColumn4SubObjByIdentityIncrement(
            LinkedHashMap<List<String>, FieldAndAnnotationVO> fieldNames2fieldMeta, Object[] row,
            List<String> fieldNames, FieldAndAnnotationVO parent,
            Collection<? extends IColumnGatheredSubType> subObjects, ColumnGatheredSubType gatheredSubType) {

        ObjectIdentityStrategy identityStrategy = gatheredSubType.objectIdentityStrategy();

        String metaFieldName = fieldNames.get(fieldNames.size() - 1);

        AtomicInteger objIdentityAtomic = new AtomicInteger(identityStrategy.autoIncrementStart());

        // 将子对象的值逐个设置到excel字段中
        subObjects.forEach(subObject -> {
            AtomicInteger indexAtomic = new AtomicInteger(-1);
            fieldNames2fieldMeta.forEach((fieldNames4Sub, fieldMeta4Sub) -> {
                int index = indexAtomic.incrementAndGet();
                // 如果是子对象匹配规则的字段（【对象类型】-【对象标识】）
                if (Objects.equals(fieldMeta4Sub.getParent(), parent)
                        && row[index] == null) {
                    DynamicColumnAnalysis subFieldAnnotation = (DynamicColumnAnalysis) fieldMeta4Sub.getAnnotation();
                    String buildFieldName = gatheredSubType.subTypeIdentity() + gatheredSubType.separator()
                            + objIdentityAtomic.getAndIncrement() + gatheredSubType.separator()
                            + subFieldAnnotation.subFieldIdentity();

                    if (Objects.equals(metaFieldName, buildFieldName)) {
                        try {
                            row[index] = fieldMeta4Sub.getField().get(subObject);
                        } catch (IllegalAccessException e) {
                            log.error("子对象字段设置失败！ ex = {}", e.getMessage());
                        }
                    }
                }
            });
        });
    }

    private void fillExcelColumn4SubObjByIdentityEnum(
            LinkedHashMap<List<String>, FieldAndAnnotationVO> fieldNames2fieldMeta, Object[] row,
            List<String> fieldNames, FieldAndAnnotationVO parent,
            Collection<? extends IColumnGatheredSubType> subObjects, ColumnGatheredSubType gatheredSubType) {

    }

    private void fillNonGatheredFields(Object dynamicColumnObject, Object[] row, List<String> fieldNames,
                                       FieldAndAnnotationVO fieldMeta, int fieldIndex) {
        ExcelProperty excelProperty = (ExcelProperty) fieldMeta.getAnnotation();
        // value是为了支持多级表头或多个列，这里对象来自同一个地方，所以可以直接比较相等性
        if (Objects.equals(fieldNames, Arrays.asList(excelProperty.value()))) {
            try {
                // 找到所属的字段并设置
                Object parentFiledObj = fieldMeta.getField().get(dynamicColumnObject);
                row[fieldIndex] = parentFiledObj;
            } catch (IllegalAccessException e) {
                log.error("动态对象输出解析，解析父对象，权限访问异常！");
            }
        }
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
                case ObjectIdentityStrategy.STRATEGY_INCREMENT:
                    for (AtomicInteger ia = new AtomicInteger(objectIdentityStrategy.autoIncrementStart());
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
                case ObjectIdentityStrategy.STRATEGY_ENUM_RANGE:
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
