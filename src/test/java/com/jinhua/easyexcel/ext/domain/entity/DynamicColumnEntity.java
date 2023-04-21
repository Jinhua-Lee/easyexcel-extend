package com.jinhua.easyexcel.ext.domain.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinhua.easyexcel.ext.annotation.CollectionGathered;
import com.jinhua.easyexcel.ext.annotation.ColumnGatheredSubType;
import com.jinhua.easyexcel.ext.annotation.DynamicColumnAnalysis;
import lombok.*;

import java.util.List;

/**
 * 动态列的实体模拟
 *
 * @author Jinhua-Lee
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicColumnAnalysis
public class DynamicColumnEntity {

    @ExcelProperty(value = "id")
    private Integer id;
    @ExcelProperty(value = "name")
    private String name;

    @CollectionGathered
    private List<ColumnGatheredEntity> columnGatheredEntities;

    @CollectionGathered
    private List<AnotherColumnGatheredEntity> anotherColumnGatheredEntities;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ColumnGatheredSubType(subTypeIdentity = "dy", separator = '_')
    public static class ColumnGatheredEntity implements IColumnGatheredSubType {

        /**
         * 用作子对象识别标识
         */
        private transient String subTypeIdentity;

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
        private transient String subTypeIdentity;

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
