package com.cet.matterhorn.easyexcel.ext.domain.valobj.meta;

import com.alibaba.excel.annotation.ExcelProperty;
import com.cet.matterhorn.easyexcel.ext.annotation.CollectionGathered;
import com.cet.matterhorn.easyexcel.ext.annotation.ColumnGatheredSubType;
import com.cet.matterhorn.easyexcel.ext.annotation.DynamicColumnAnalysis;
import com.cet.matterhorn.easyexcel.ext.annotation.ObjectIdentityStrategy;
import com.cet.matterhorn.easyexcel.ext.domain.entity.IColumnGatheredSubType;
import com.cet.matterhorn.easyexcel.ext.util.StringUtil;
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
        if (!dynamicAnalysisEnabled(type)) {
            throw new IllegalStateException(
                    String.format("type = %s is not suitable for dynamic column analysis." +
                            " Make sure the Type is annotated with @DynamicColumnAnalysis.", type)
            );
        }
        // 2. 构造父类型
        buildParentMeta(type);
    }

    /**
     * 校验某个类型及其父类型，是否有开启动态列解析
     *
     * @param type 类型
     * @return 是否开启动态列解析
     */
    private boolean dynamicAnalysisEnabled(Class<?> type) {
        if (type == Object.class) {
            return false;
        }
        return type.isAnnotationPresent(DynamicColumnAnalysis.class)
                || dynamicAnalysisEnabled(type.getSuperclass());
    }

    private void buildParentMeta(Class<?> type) {
        // 2. 父类型的的非聚合属性列表
        Set<Field> declaredFields = new LinkedHashSet<>();
        resolveAllDeclaredFieldsUpwards(declaredFields, type);

        this.nonGatheredFieldsAndAnnotations = declaredFields.stream()
                // 外部访问权限
                .peek(field -> field.setAccessible(true))
                .filter(field ->
                        field.isAnnotationPresent(ExcelProperty.class)
                                && !ObjectUtils.isEmpty(field.getAnnotation(ExcelProperty.class).value())
                ).map(field ->
                        new FieldAndAnnotationVO(field, field.getAnnotation(ExcelProperty.class), null)
                ).sorted((f1, f2) -> {
                    ExcelProperty f1ExcelProp = (ExcelProperty) f1.getAnnotation();
                    ExcelProperty f2ExcelProp = (ExcelProperty) f2.getAnnotation();
                    return new ExcelPropertyComparator().compare(f1ExcelProp, f2ExcelProp);
                }).collect(Collectors.toCollection(LinkedHashSet::new));
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
                }).collect(Collectors.toCollection(LinkedHashSet::new));

        // 3. 校验集合引用的子类型的重复
        duplicationCheckOfCollectionRefTypeThrows();

        // 4. 校验集合注解属性：重复性，策略的字段关系
        subTypeAnnotationCheckOfDuplicationAndIdentityStrategy(type);
    }

    private void resolveAllDeclaredFieldsUpwards(Set<Field> declaredFields, Class<?> type) {
        if (type == Object.class) {
            return;
        }
        declaredFields.addAll(Arrays.asList(type.getDeclaredFields()));
        resolveAllDeclaredFieldsUpwards(declaredFields, type.getSuperclass());
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

    private void subTypeAnnotationCheckOfDuplicationAndIdentityStrategy(Class<?> parentType) {
        List<ColumnGatheredSubType> gatheredSubTypeAnnotations = this.gatheredFieldsAndAnnotations.stream()
                .filter(Objects::nonNull)
                .map(FieldAndAnnotationWithGenericType::getSubTypeAndFields)
                // 子类型及注解
                .map(SubTypeAndFieldsVO::getTypeAndAnnotation)
                .map(typeAndAnnotationVO -> (ColumnGatheredSubType) typeAndAnnotationVO.getAnnotation())
                .collect(Collectors.toList());

        // 1. 对象标识【subTypeIdentity】的重复性
        Set<String> subTypeIdentities = gatheredSubTypeAnnotations.stream()
                .map(ColumnGatheredSubType::subTypeIdentity).collect(Collectors.toSet());
        if (subTypeIdentities.size() != gatheredSubTypeAnnotations.size()) {
            throw new IllegalStateException(
                    String.format("There are duplication annotation property(subTypeIdentity)" +
                                    " among subtypes of parentType = %s, which is not allowed. Please check.",
                            parentType
                    )
            );
        }

        // 2. 校验注解策略配置与属性字段关系
        gatheredSubTypeAnnotations.forEach(gatheredSubTypeAnnotation -> {
            ObjectIdentityStrategy objIdentityStrategy = gatheredSubTypeAnnotation.objectIdentityStrategy();
            // 2.1 为给定范围枚举策略时，枚举范围值不允许为empty
            final int enumStrategy = 2;
            if (objIdentityStrategy.value() == enumStrategy) {
                if (ObjectUtils.isEmpty(objIdentityStrategy.objectIdentityRange())) {
                    throw new IllegalStateException(String.format(
                            "There are some illegal annotation configurations in subType, parentType = %s",
                            parentType
                    ));
                }
            }
            // 2.2 策略之间的冲突：枚举类型每个元素都不允许是纯数字
            for (String identityEnumItem : objIdentityStrategy.objectIdentityRange()) {
                if (StringUtil.ofNumeric(identityEnumItem)) {
                    throw new IllegalStateException(
                            String.format(
                                    "the enumeration string is not allowed to be of pure numbers." +
                                            " please check parentType(%s)",
                                    parentType
                            )
                    );
                }
            }
        });
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
                            field.set(dynamicColumnEntity, new LinkedHashSet<>());
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

    public LinkedHashMap<FieldAndAnnotationWithGenericType, Integer> maxColFieldNumMap4Entity(Collection<?> entities) {
        LinkedHashMap<FieldAndAnnotationWithGenericType, Integer> maxColFieldNumMap = new LinkedHashMap<>();

        // 每个对象中每个字段的最大数量
        entities.forEach(entity -> {
            Map<FieldAndAnnotationWithGenericType, Integer> colFieldNumMap = numMap4CollectionGatheredField(entity);
            colFieldNumMap.forEach((faaWithGeneric, num) ->
                    maxColFieldNumMap.compute(faaWithGeneric, (fieldAndAnnotation, number) -> {
                        number = number == null ? 0 : number;
                        if (num > number) {
                            return num;
                        }
                        return number;
                    })
            );
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
        if (!dynamicAnalysisEnabled(object.getClass())) {
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

    private static class ExcelPropertyComparator implements Comparator<ExcelProperty> {
        @Override
        public int compare(ExcelProperty exProp1, ExcelProperty exProp2) {
            int cmpIndex = compareIndex(exProp1, exProp2);
            return cmpIndex == 0 || cmpIndex == -1 ? compareOrder(exProp1, exProp2) : cmpIndex;
        }

        private int compareIndex(ExcelProperty exProp1, ExcelProperty exProp2) {
            if (exProp1.index() >= 0 && exProp2.index() >= 0) {
                if (exProp1.index() == exProp2.index()) {
                    return 0;
                }
                return exProp1.index() - exProp2.index();
            } else if (exProp1.index() < 0 && exProp2.index() < 0) {
                return exProp1.index() - exProp2.index();
            } else if (exProp1.index() < 0) {
                return 1;
            } else {
                return -1;
            }
        }

        private int compareOrder(ExcelProperty exProp1, ExcelProperty exProp2) {
            return exProp1.order() - exProp2.order();
        }
    }
}
