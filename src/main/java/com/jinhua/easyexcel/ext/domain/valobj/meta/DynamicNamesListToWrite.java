package com.jinhua.easyexcel.ext.domain.valobj.meta;

import java.util.List;

/**
 * @author Jinhua-Lee
 */
public class DynamicNamesListToWrite implements DynamicMetaToWrite {

    /**
     * Excel中二位数组来存放列名，每个列名可以给多个名字
     */
    List<List<String>> cellNamesList;
}
