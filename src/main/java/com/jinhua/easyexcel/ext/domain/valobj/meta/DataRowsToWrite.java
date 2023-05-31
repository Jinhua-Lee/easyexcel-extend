package com.jinhua.easyexcel.ext.domain.valobj.meta;

import java.util.List;

/**
 * @author Jinhua-Lee
 */
public class DataRowsToWrite implements DynamicDataToWrite {
    /**
     * Excel数据，一维数组表示多行，二维数组表示多列
     */
    List<List<Object>> dataRows;
}
