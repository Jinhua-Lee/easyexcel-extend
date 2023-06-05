package com.jinhua.easyexcel.ext.domain.valobj.meta.out;

import lombok.*;

/**
 * @author Jinhua-Lee
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicMetaAndDataToWrite {

    private DynamicMetaWrapper metaToWrite;
    private DynamicDataWrapper dataToWrite;
}
