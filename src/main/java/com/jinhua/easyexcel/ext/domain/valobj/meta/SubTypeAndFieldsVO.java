package com.jinhua.easyexcel.ext.domain.valobj.meta;

import com.jinhua.easyexcel.ext.annotation.ColumnGatheredSubType;
import com.jinhua.easyexcel.ext.annotation.DynamicColumnAnalysis;
import com.jinhua.easyexcel.ext.domain.entity.IColumnGatheredSubType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Jinhua-Lee
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class SubTypeAndFieldsVO extends BaseTypeAndFieldsVO {

    /**
     * DynamicColumnAnalysis 注解标识的field及注解
     */
    Set<FieldAndAnnotationVO> subFieldAndAnnotations;

    public SubTypeAndFieldsVO(Class<?> type, FieldAndAnnotationVO parent) {
        super(type, ColumnGatheredSubType.class);
        buildSubMeta(type, parent);
    }

    private void buildSubMeta(Class<?> type, FieldAndAnnotationVO parent) {
        this.subFieldAndAnnotations = Arrays.stream(type.getDeclaredFields())
                // 外部访问权限
                .peek(field -> field.setAccessible(true))
                .filter(field ->
                        field.isAnnotationPresent(DynamicColumnAnalysis.class)
                                && !ObjectUtils.isEmpty(
                                field.getAnnotation(DynamicColumnAnalysis.class).subFieldIdentity()
                        )
                ).map(field ->
                        new FieldAndAnnotationVO(
                                field, field.getAnnotation(DynamicColumnAnalysis.class), parent
                        )
                ).collect(Collectors.toCollection(LinkedHashSet::new));
        // 1. 校验注解属性的重复性
        checkAnnotationPropertyDuplicationThrows();
        // 2. 校验子类型实现某接口
        Class<IColumnGatheredSubType> baseSubClazz = IColumnGatheredSubType.class;
        colRefTypeImplCheckThrows(baseSubClazz);
    }

    private void checkAnnotationPropertyDuplicationThrows() {
        Set<String> subFieldIdentities = this.subFieldAndAnnotations.stream().map(fieldAndAnnotationVO ->
                (DynamicColumnAnalysis) fieldAndAnnotationVO.getAnnotation()
        ).map(DynamicColumnAnalysis::subFieldIdentity).collect(Collectors.toSet());
        if (this.subFieldAndAnnotations.size() != subFieldIdentities.size()) {
            throw new IllegalArgumentException(
                    String.format("there were duplications in subtype annotation properties of %s",
                            this.typeAndAnnotation.getType()
                    )
            );
        }
    }

    private void colRefTypeImplCheckThrows(Class<?> baseSubClazz) {

        if (!this.typeAndAnnotation.implTypeOf(baseSubClazz)) {
            throw new IllegalStateException(
                    String.format("the subtype classes which was used for dynamic column handling" +
                                    " should implement IColumnGatheredSubType! classes = %s ",
                            this.typeAndAnnotation.getType()
                    )
            );
        }
    }

    public Optional<? extends FieldAndAnnotationVO> matchedSubFieldAndAnnotation(String cellFieldName) {
        return this.subFieldAndAnnotations.stream().filter(faa -> {
            ColumnGatheredSubType columnGatheredSubType =
                    (ColumnGatheredSubType) this.typeAndAnnotation.getAnnotation();
            DynamicColumnAnalysis curSubFieldAnnotation = (DynamicColumnAnalysis) faa.getAnnotation();
            return cellFieldName.startsWith(
                    columnGatheredSubType.subTypeIdentity() + columnGatheredSubType.separator()
            ) && cellFieldName.endsWith(
                    columnGatheredSubType.separator() + curSubFieldAnnotation.subFieldIdentity()
            );
        }).findFirst();
    }
}
