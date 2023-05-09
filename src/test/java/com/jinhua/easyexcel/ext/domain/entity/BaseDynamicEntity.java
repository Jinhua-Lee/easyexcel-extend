package com.jinhua.easyexcel.ext.domain.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public abstract class BaseDynamicEntity {

    @ExcelProperty(value = "id")
    private Integer id;
}
