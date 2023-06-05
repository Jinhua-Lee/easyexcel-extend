package com.jinhua.easyexcel.ext.domain.valobj.meta.out;

import java.util.List;

/**
 * Excel中元数据
 * <p>主要是指字段名信息，用于Excel导出时候用作head信息
 *
 * @author Jinhua-Lee
 */
public interface DynamicMetaWrapper {

    /**
     * 获取【动态对象Meta信息】
     *
     * @return 【动态对象Meta信息】
     */
    List<?> getDynamicMeta();
}
