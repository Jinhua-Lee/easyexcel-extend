package com.cet.matterhorn.easyexcel.ext.domain.service.convertor.impl;

import com.alibaba.excel.annotation.ExcelProperty;
import com.cet.matterhorn.easyexcel.ext.domain.entity.meta.DynamicColumnAnalysisInfo;
import com.cet.matterhorn.easyexcel.ext.domain.valobj.meta.TypeAndAnnotationVO;
import com.cet.matterhorn.easyexcel.ext.annotation.ColumnGatheredSubType;
import com.cet.matterhorn.easyexcel.ext.domain.entity.IColumnGatheredSubType;
import com.cet.matterhorn.easyexcel.ext.domain.service.convertor.CellMap2CustomWrappedEntityConvertor;
import com.cet.matterhorn.easyexcel.ext.domain.service.convertor.CellString2FieldSetter;
import com.cet.matterhorn.easyexcel.ext.domain.valobj.meta.FieldAndAnnotationVO;
import com.cet.matterhorn.easyexcel.ext.domain.valobj.meta.FieldAndAnnotationWithGenericType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jinhua-Lee
 */
@Slf4j
@Component
@SuppressWarnings("unused")
public class CellMap2CustomWrappedEntityConvertorImpl implements CellMap2CustomWrappedEntityConvertor {

    /**
     * 通过缓存来减少解析过程
     */
    private final Map<Class<?>, DynamicColumnAnalysisInfo> dynamicColumnAnalysisInfos = new ConcurrentHashMap<>();
    private CellString2FieldSetter cellString2FieldSetter;

    @Override
    public <T> T cellMap2CustomWrappedEntity(Map<Integer, String> cellIndex2Data,
                                             Map<Integer, String> index2Head, Class<T> customWrappedClass)
            throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {

        // 1. 实例化指定类型的对象
        T customWrappedEntity = instantiate(customWrappedClass);

        // 保证已有
        DynamicColumnAnalysisInfo analysisInfo = dynamicColumnAnalysisInfos.compute(customWrappedClass,
                (clazz, aInfo) -> {
                    if (aInfo == null) {
                        aInfo = new DynamicColumnAnalysisInfo(customWrappedClass);
                    }
                    return aInfo;
                });
        // 初始化集合类型的字段
        analysisInfo.getParent().initCollectionFields4Entity(customWrappedEntity);

        // 2. 进行自定义封装处理
        // 对于非封装的字段，按匹配的@ExcelProperty的value值去做
        Set<FieldAndAnnotationVO> nonGatheredFieldAndAnnotations =
                analysisInfo.getParent().getNonGatheredFieldsAndAnnotations();
        // 对于封装的字段，类型是集合类型，且类型上有注解
        Set<FieldAndAnnotationWithGenericType> gatheredFieldsAndAnnotations
                = analysisInfo.getParent().getGatheredFieldsAndAnnotations();

        // 找到excel字段值，作用于java实体
        for (Map.Entry<Integer, String> entry : cellIndex2Data.entrySet()) {
            Integer colIndex = entry.getKey();
            String colValue = entry.getValue();
            // 对于null字段，暂不建立对象
            if (colValue == null) {
                continue;
            }
            String cellFieldName = Optional.ofNullable(index2Head.get(colIndex))
                    .orElseThrow(() -> new NoSuchElementException("No value present"));

            // 1. 找到对应的非聚合字段，作用于根实体

            Optional<FieldAndAnnotationVO> matchedNonGathered = nonGatheredFieldAndAnnotations.stream()
                    .filter(fieldAndAnnotationVO -> {
                        String[] values = ((ExcelProperty) fieldAndAnnotationVO.getAnnotation()).value();
                        return Arrays.asList(values).contains(cellFieldName);
                    }).findFirst();
            if (matchedNonGathered.isPresent()) {
                // 非对象聚合的，则匹配指定字段并且设置值
                matchedNonGathered.get().setCellStringField4Entity(customWrappedEntity, colValue,
                        this.cellString2FieldSetter
                );
                continue;
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("there is no non-gathered properties in class = {}", customWrappedClass);
                }
            }

            // 3. 尝试将值设置到子对象的字段中。
            //      3.1 找到字段。
            Optional<FieldAndAnnotationWithGenericType> faaWithGenericOpt = gatheredFieldsAndAnnotations.stream()
                    // 找到字段
                    .filter(faaWithGeneric ->
                            faaWithGeneric.matchedSubFieldAndAnnotation(cellFieldName).isPresent()
                    ).findFirst();
            if (faaWithGenericOpt.isPresent()) {
                //      3.2 保证子对象的存在性，以及与父对象的关联性；
                FieldAndAnnotationWithGenericType faaWithGeneric = faaWithGenericOpt.get();
                Collection<IColumnGatheredSubType> parentFieldCollection = analysisInfo.getParent()
                        .findCollectionByFieldFromEntity(customWrappedEntity, faaWithGeneric.getField());
                IColumnGatheredSubType subObject = getSubObjectOrCreate(cellFieldName,
                        parentFieldCollection,
                        faaWithGeneric.getSubTypeAndFields().getTypeAndAnnotation()
                );
                //      3.3 通过反射设置值。
                Optional<? extends FieldAndAnnotationVO> faaByCellFieldNameOpt =
                        faaWithGeneric.getSubTypeAndFields().matchedSubFieldAndAnnotation(cellFieldName);
                if (faaByCellFieldNameOpt.isPresent()) {
                    faaByCellFieldNameOpt.get().setCellStringField4Entity(subObject, colValue,
                            this.cellString2FieldSetter
                    );
                } else {
                    log.warn("the field name {} doesn't match any field of class {}", cellFieldName,
                            faaWithGeneric.getSubTypeAndFields().getTypeAndAnnotation().getType());
                }
            } else {
                log.warn("failed to find field for cell-field = {}", cellFieldName);
            }
        }
        return customWrappedEntity;
    }

    /**
     * 获取集合的指定子类型实例对象，找不到，则新建一个
     *
     * @param cellFieldName        excel中的字段名
     * @param fieldCollection      集合字段
     * @param subTypeAndAnnotation 子类型和注解信息
     * @return 子类型的实例对象
     */
    private IColumnGatheredSubType getSubObjectOrCreate(String cellFieldName,
                                                        Collection<IColumnGatheredSubType> fieldCollection,
                                                        TypeAndAnnotationVO subTypeAndAnnotation) {
        ColumnGatheredSubType dynamicColumnSubTypeAnnotation =
                (ColumnGatheredSubType) subTypeAndAnnotation.getAnnotation();
        // 截取前两个作为对象标识【对象序列】-属性
        String subObjectIdentity = cellFieldName.trim().substring(
                0,
                cellFieldName.lastIndexOf(dynamicColumnSubTypeAnnotation.separator())
        );
        return fieldCollection.stream().filter(subObj ->
                Objects.equals(subObj.getSubObjectIdentity(), subObjectIdentity)
        ).findFirst().orElseGet(() -> {
            // 实例化指定类型的对象
            try {
                IColumnGatheredSubType iColumnGatheredSubType =
                        (IColumnGatheredSubType) subTypeAndAnnotation.getType().getDeclaredConstructor().newInstance();
                iColumnGatheredSubType.setSubObjectIdentity(subObjectIdentity);
                // 加到已有集合中
                fieldCollection.add(iColumnGatheredSubType);
                return iColumnGatheredSubType;
            } catch (InstantiationException | IllegalAccessException
                    | InvocationTargetException | NoSuchMethodException e) {
                log.error("动态解析对象的实例化失败！message = {}", e.getMessage());
                return null;
            }
        });
    }

    private <T> T instantiate(Class<T> customWrappedClass)
            throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        T dynamicColumnEntity;
        try {
            dynamicColumnEntity = customWrappedClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | InvocationTargetException
                | IllegalAccessException | NoSuchMethodException e) {
            log.error("动态解析对象的实例化失败！message = {}", e.getMessage());
            throw e;
        }
        Objects.requireNonNull(dynamicColumnEntity);
        return dynamicColumnEntity;
    }

    @Autowired
    public void setCellString2FieldSetter(CellString2FieldSetter cellString2FieldSetter) {
        this.cellString2FieldSetter = cellString2FieldSetter;
    }
}
