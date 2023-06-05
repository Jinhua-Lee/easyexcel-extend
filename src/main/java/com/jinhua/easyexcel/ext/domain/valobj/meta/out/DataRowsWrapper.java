package com.jinhua.easyexcel.ext.domain.valobj.meta.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Jinhua-Lee
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataRowsWrapper implements DynamicDataWrapper {
    /**
     * Excel数据，一维数组表示多行，二维数组表示多列
     */
    List<List<Object>> dataRows;

    @Override
    public List<?> getDynamicData() {
        return dataRows;
    }
}
