package com.cet.matterhorn.easyexcel.ext.domain.entity;

/**
 * @author Jinhua-Lee
 */
public interface IOperationWithEntity<T> {

    /**
     * 对实体要做的操作
     *
     * @param entity 解析出的实体
     */
    void operate(T entity);
}
