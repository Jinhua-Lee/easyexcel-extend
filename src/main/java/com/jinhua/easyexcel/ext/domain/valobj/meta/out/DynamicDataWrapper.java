package com.jinhua.easyexcel.ext.domain.valobj.meta.out;

import java.util.List;

/**
 * Excel待写出的数据
 *
 * @author Jinhua-Lee
 */
public interface DynamicDataWrapper {

    /**
     * 获取【动态对象数据行集合】
     *
     * @return 【动态对象数据行集合】
     */
    List<?> getDynamicData();
}
