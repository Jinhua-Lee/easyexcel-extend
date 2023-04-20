package com.jinhua.easyexcel.ext.web.controller.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 模拟动态列的数据传输
 * @author Jinhua-Lee
 */
@Data
public class DynamicColumnDTO {

    @ExcelProperty(value = "id")
    private Integer id;
    @ExcelProperty(value = "name")
    private String name;
    @ExcelProperty(value = "dy_1_a")
    private String dy1a;
    @ExcelProperty(value = "dy_1_b")
    private String dy1b;
    @ExcelProperty(value = "dy_2_a")
    private String dy2a;
    @ExcelProperty(value = "dy_2_b")
    private String dy2b;
}
