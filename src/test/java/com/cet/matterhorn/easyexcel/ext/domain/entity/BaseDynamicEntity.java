package com.cet.matterhorn.easyexcel.ext.domain.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.cet.matterhorn.easyexcel.ext.annotation.CollectionGathered;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class BaseDynamicEntity extends BaseExcelEntity {

    @ExcelProperty(value = "name")
    private String name;

    @CollectionGathered
    private Set<DynamicColumnEntity.ColumnGatheredEntity> columnGatheredEntities;
}
