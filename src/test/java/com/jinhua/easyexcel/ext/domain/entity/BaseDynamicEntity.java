package com.jinhua.easyexcel.ext.domain.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.jinhua.easyexcel.ext.annotation.DynamicColumnAnalysis;
import lombok.Data;

@Data
@DynamicColumnAnalysis
public abstract class BaseDynamicEntity {

    @ExcelProperty(value = "id")
    private Integer id;
}
