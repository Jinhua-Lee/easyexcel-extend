package com.jinhua.easyexcel.ext.domain.service.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.jinhua.easyexcel.ext.domain.entity.IOperationWithEntity;
import com.jinhua.easyexcel.ext.domain.service.convertor.CellMap2CustomWrappedEntityConvertor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * ExcelMap转对象的监听器
 *
 * @author Jinhua-Lee
 */
@Slf4j
@Component
public class DefaultMap2CustomEntityListener<T> extends AnalysisEventListener<Map<Integer, String>> {

    /**
     * 第一行元信息【列索引】 -> 【列名】
     */
    private Map<Integer, String> index2HeadName;

    private final Class<T> customWrappedEntityClass;
    private final IOperationWithEntity<T> operation;
    private final CellMap2CustomWrappedEntityConvertor cellMap2CustomWrappedEntityConvertor;

    @Autowired
    public DefaultMap2CustomEntityListener(Class<T> customWrappedEntityClass,
                                           IOperationWithEntity<T> entityOperation,
                                           CellMap2CustomWrappedEntityConvertor cellMap2CustomWrappedEntityConvertor) {
        this.customWrappedEntityClass = customWrappedEntityClass;
        this.operation = entityOperation;
        this.cellMap2CustomWrappedEntityConvertor = cellMap2CustomWrappedEntityConvertor;
    }

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {

        if (context.readRowHolder().getRowIndex() == 0) {
            this.index2HeadName = data;
        } else {
            handleData(data, index2HeadName, this.operation);
        }
    }

    protected void handleData(Map<Integer, String> data, Map<Integer, String> index2HeadName,
                              IOperationWithEntity<T> operation) {
        // - excel根据列index去识别数据；
        // - 对象解析通过列名去做。
        T entity = null;
        try {
            entity = this.cellMap2CustomWrappedEntityConvertor.cellMap2CustomWrappedEntity(
                    data, index2HeadName, customWrappedEntityClass
            );
        } catch (InvocationTargetException | InstantiationException
                | IllegalAccessException | NoSuchMethodException e) {
            log.error("map转指定的实体失败！exMessage = {}", e.getMessage());
        }

        operation.operate(entity);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
    }

}
