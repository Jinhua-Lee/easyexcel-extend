package com.jinhua.easyexcel.ext.domain.valobj.meta;

import com.alibaba.excel.annotation.ExcelProperty;
import com.jinhua.easyexcel.ext.annotation.CollectionGathered;
import com.jinhua.easyexcel.ext.annotation.DynamicColumnAnalysis;
import com.jinhua.easyexcel.ext.domain.entity.IColumnGatheredSubType;
import com.jinhua.easyexcel.ext.domain.entity.meta.DynamicColumnAnalysisInfo;
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
        if (!type.isAnnotationPresent(DynamicColumnAnalysis.class)
                && !type.getSuperclass().isAnnotationPresent(DynamicColumnAnalysis.class)) {
            throw new UnsupportedOperationException(
                    String.format("类型未允许自定义解析！ class = %s", type)
            );
        }
        // 2. 构造父类型
        buildParentMeta(type);
    }

    private void buildParentMeta(Class<?> type) {
        // 2. 父类型的的非聚合属性列表
        Set<Field> declaredFields = new HashSet<>();
        declaredFields.addAll(Arrays.asList(type.getDeclaredFields()));
        declaredFields.addAll(Arrays.asList(type.getSuperclass().getDeclaredFields()));

        this.nonGatheredFieldsAndAnnotations = declaredFields.stream()
                // 外部访问权限
                .peek(field -> field.setAccessible(true))
                .filter(field ->
                        field.isAnnotationPresent(ExcelProperty.class)
                                && !ObjectUtils.isEmpty(field.getAnnotation(ExcelProperty.class).value())
                ).map(field ->
                        new FieldAndAnnotationVO(field, field.getAnnotation(ExcelProperty.class))
                ).collect(Collectors.toSet());
        // 3. 【父类型的聚合属性 + 引用的子类型】列表
        this.gatheredFieldsAndAnnotations = declaredFields.stream()
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

    public Map<FieldAndAnnotationWithGenericType, Integer> maxColFieldNumMap4Entity(Collection<?> entities) {
        Map<FieldAndAnnotationWithGenericType, Integer> maxColFieldNumMap = new LinkedHashMap<>();

        // 每个对象中每个字段的最大数量
        entities.forEach(entity -> {
            Map<FieldAndAnnotationWithGenericType, Integer> colFieldNumMap = numMap4CollectionGatheredField(entity);

            colFieldNumMap.forEach((faaWithGeneric, num) -> {

                maxColFieldNumMap.compute(faaWithGeneric, (fieldAndAnnotation, number) -> {
                    number = number == null ? 0 : number;
                    if (num > number) {
                        return num;
                    }
                    return number;
                });
            });
        });
        return maxColFieldNumMap;
    }

    /**
     * 获取某对象的@CollectionGather字段的数目
     *
     * @param object 对象
     * @return 某对象的@CollectionGather字段的数目
     */
    public Map<FieldAndAnnotationWithGenericType, Integer> numMap4CollectionGatheredField(Object object) {
        if (!object.getClass().isAnnotationPresent(DynamicColumnAnalysis.class)) {
            throw new IllegalStateException(
                    String.format("type = %s is not suitable for dynamic column analysis." +
                            " Make sure the Type is annotated with @DynamicColumnAnalysis.", object.getClass())
            );
        }
        Map<FieldAndAnnotationWithGenericType, Integer> colFieldNumMap =
                new LinkedHashMap<>(gatheredFieldsAndAnnotations.size());
        this.gatheredFieldsAndAnnotations.forEach(gFieldAnnotation -> {
            Field colField = gFieldAnnotation.getField();
            try {
                Collection<?> colFieldVal = (Collection<?>) colField.get(object);
                colFieldNumMap.put(gFieldAnnotation,
                        Optional.ofNullable(colFieldVal)
                                .orElse(Collections.emptySet()).size()
                );
            } catch (IllegalAccessException ignored) {
            }
        });
        return colFieldNumMap;
    }
}
