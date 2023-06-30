package com.cet.matterhorn.easyexcel.ext.domain.valobj.meta.out;

import lombok.*;

import java.util.List;

/**
 * @author Jinhua-Lee
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DynamicNamesListWrapper implements DynamicMetaWrapper {

    /**
     * Excel中二位数组来存放列名，每个列名可以给多个名字
     */
    private List<List<String>> cellNamesList;

    @Override
    public List<?> getDynamicMeta() {
        return this.cellNamesList;
    }
}
