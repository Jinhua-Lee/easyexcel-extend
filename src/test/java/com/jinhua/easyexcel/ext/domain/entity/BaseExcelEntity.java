package com.jinhua.easyexcel.ext.domain.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.jinhua.easyexcel.ext.annotation.DynamicColumnAnalysis;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@DynamicColumnAnalysis
public abstract class BaseExcelEntity {

    @ExcelProperty(value = "id")
    private Integer id;
}
