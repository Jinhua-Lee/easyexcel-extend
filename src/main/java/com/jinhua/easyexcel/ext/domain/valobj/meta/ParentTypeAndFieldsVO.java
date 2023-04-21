package com.jinhua.easyexcel.ext.domain.valobj.meta;

import com.alibaba.excel.annotation.ExcelProperty;
import com.jinhua.easyexcel.ext.annotation.CollectionGathered;
import com.jinhua.easyexcel.ext.annotation.DynamicColumnAnalysis;
import com.jinhua.easyexcel.ext.domain.entity.IColumnGatheredSubType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Jinhua-Lee
 */
@Slf4j
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class ParentTypeAndFieldsVO extends BaseTypeAndFieldsVO {

    /**
     * ExcelProperty 注解的属性信息
     */
    private Set<FieldAndAnnotationVO> nonGatheredFieldsAndAnnotations;
    /**
     * CollectionGathered 注解及属性信息
     */
    private Set<FieldAndAnnotationWithGenericType> gatheredFieldsAndAnnotations;

    /**
     * 父对象构造
     *
     * @param type 外层对象类型
     */
    public ParentTypeAndFieldsVO(Class<?> type) {
        super(type, DynamicColumnAnalysis.class);
        // 1. 判断【动态列解析】的开启状态
        if (!type.isAnnotationPresent(DynamicColumnAnalysis.class)) {
            throw new UnsupportedOperationException(
                    String.format("类型未允许自定义解析！ class = %s", type)
            );
        }
        // 2. 构造父类型
        buildParentMeta(type);
    }

    private void buildParentMeta(Class<?> type) {
        // 2. 父类型的的非聚合属性列表
        this.nonGatheredFieldsAndAnnotations = Arrays.stream(type.getDeclaredFields())
                // 外部访问权限
                .peek(field -> field.setAccessible(true))
                .filter(field ->
                        field.isAnnotationPresent(ExcelProperty.class)
                                && !ObjectUtils.isEmpty(field.getAnnotation(ExcelProperty.class).value())
                ).map(field ->
                        new FieldAndAnnotationVO(field, field.getAnnotation(ExcelProperty.class))
                ).collect(Collectors.toSet());
        // 3. 【父类型的聚合属性 + 引用的子类型】列表
        this.gatheredFieldsAndAnnotations = Arrays.stream(type.getDeclaredFields())
                // 外部访问权限
                .peek(field -> field.setAccessible(true))
                .filter(field ->
                        field.isAnnotationPresent(CollectionGathered.class)
                ).map(field -> {
                    // 集合字段的引用类型
                    return new FieldAndAnnotationWithGenericType(
                            field, field.getAnnotation(CollectionGathered.class),
                            resolveGenericRefType4Field(field)
                    );
                }).collect(Collectors.toSet());

        // 3. 校验集合引用的子类型的重复
        duplicationCheckOfCollectionRefTypeThrows();
    }

    /**
     * 获取field的泛型引用类型
     *
     * @param field 字段field
     * @return 对应的泛型引用类型
     */
    private Class<?> resolveGenericRefType4Field(Field field) {
        ParameterizedType paramType =
                ((ParameterizedType) field.getGenericType());
        return (Class<?>) paramType.getActualTypeArguments()[0];
    }

    /**
     * 校验集合引用的子类型的重复
     */
    private void duplicationCheckOfCollectionRefTypeThrows() {
        long parentFieldRefSubTypeCount = this.gatheredFieldsAndAnnotations.stream()
                .map(FieldAndAnnotationWithGenericType::getSubTypeAndFields)
                .distinct().count();
        if (parentFieldRefSubTypeCount != this.gatheredFieldsAndAnnotations.size()) {
            throw new IllegalStateException(String.format(
                    "there were duplicated generic type found for class = %s which were" +
                            " annotated with @CollectionGathered, and it is not allowed.",
                    this.typeAndAnnotation.getType()
            ));
        }
    }

    public void initCollectionFields4Entity(Object dynamicColumnEntity) {
        this.gatheredFieldsAndAnnotations.stream()
                .map(FieldAndAnnotationVO::getField)
                .forEach(field -> {
                    // 保证集合的初始化，默认给几个集合类型先
                    if (field.getType() == List.class || field.getType() == Collection.class) {
                        try {
                            field.set(dynamicColumnEntity, new ArrayList<>());
                        } catch (IllegalAccessException e) {
                            log.error("访问权限错误，message = {}", e.getMessage());
                        }
                    } else if (field.getType() == Set.class) {
                        try {
                            field.set(dynamicColumnEntity, new HashSet<>());
                        } catch (IllegalAccessException e) {
                            log.error("访问权限错误，message = {}", e.getMessage());
                        }
                    }
                });
        log.info("initialized collection fields in object {}, which were used for dynamic column import.",
                dynamicColumnEntity);
    }

    @SuppressWarnings("unchecked")
    public <T> Collection<IColumnGatheredSubType> findCollectionByFieldFromEntity(T customWrappedEntity,
                                                                                  Field collectionGatheredField)
            throws IllegalAccessException {

        Optional<FieldAndAnnotationWithGenericType> faaWithGenericOpt =
                this.gatheredFieldsAndAnnotations.stream().filter(faaWithGeneric ->
                        Objects.equals(faaWithGeneric.getField(), collectionGatheredField)
                ).findFirst();

        Collection<IColumnGatheredSubType> iColumnGatheredSubTypes = null;
        if (faaWithGenericOpt.isPresent()) {
            iColumnGatheredSubTypes = (Collection<IColumnGatheredSubType>)
                    collectionGatheredField.get(customWrappedEntity);
        }
        return iColumnGatheredSubTypes;
    }
}