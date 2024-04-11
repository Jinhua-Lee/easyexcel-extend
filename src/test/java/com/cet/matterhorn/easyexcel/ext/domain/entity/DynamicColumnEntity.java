package com.cet.matterhorn.easyexcel.ext.domain.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.cet.matterhorn.easyexcel.ext.annotation.CollectionGathered;
import com.cet.matterhorn.easyexcel.ext.annotation.ColumnGatheredSubType;
import com.cet.matterhorn.easyexcel.ext.annotation.DynamicColumnAnalysis;
import com.cet.matterhorn.easyexcel.ext.annotation.ObjectIdentityStrategy;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 动态列的实体模拟
 *
 * @author Jinhua-Lee
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DynamicColumnEntity extends BaseDynamicEntity {

    @CollectionGathered
    private List<AnotherColumnGatheredEntity> anotherColumnGatheredEntities;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ColumnGatheredSubType(subTypeIdentity = "dy", separator = '_',
            objectIdentityStrategy = @ObjectIdentityStrategy(value = 2, objectIdentityRange = {"打开", "闭合"})
    )
    public static class ColumnGatheredEntity implements IColumnGatheredSubType {

        /**
         * 用作子对象识别标识
         */
        private transient String subObjectIdentity;

        @DynamicColumnAnalysis(subFieldIdentity = "a")
        private String a;
        @DynamicColumnAnalysis(subFieldIdentity = "b")
        private String b;

        @Override
        @SneakyThrows
        public String toString() {
            return new ObjectMapper().writeValueAsString(this);
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ColumnGatheredSubType(subTypeIdentity = "dc", separator = '_')
    public static class AnotherColumnGatheredEntity implements IColumnGatheredSubType {

        /**
         * 用作子对象识别标识
         */
        private transient String subObjectIdentity;

        @DynamicColumnAnalysis(subFieldIdentity = "a")
        private String a;
        @DynamicColumnAnalysis(subFieldIdentity = "b")
        private String b;

        @Override
        @SneakyThrows
        public String toString() {
            return new ObjectMapper().writeValueAsString(this);
        }
    }

    @Override
    @SneakyThrows
    public String toString() {
        return new ObjectMapper().writeValueAsString(this);
    }
}
