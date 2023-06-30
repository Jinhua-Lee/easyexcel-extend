package com.cet.matterhorn.easyexcel.ext.domain.entity;

/**
 * 动态列子类型
 *
 * @author Jinhua-Lee
 */
public interface IColumnGatheredSubType {

    /**
     * 对象标识，取自Excel的列名
     *
     * @return 对象标识
     */
    String getSubTypeIdentity();

    /**
     * 设置对象标识，取自Excel的列名
     *
     * @param subTypeIdentity 对象标识
     */
    void setSubTypeIdentity(String subTypeIdentity);
}
