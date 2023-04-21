package com.jinhua.easyexcel.ext.service.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.jinhua.easyexcel.ext.domain.entity.DynamicColumnEntity;
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
public class MapDataAnalysisEventListener extends AnalysisEventListener<Map<Integer, String>> {

    private CellMap2CustomWrappedEntityConvertor cellMap2CustomWrappedEntityConvertor;

    /**
     * 第一行元信息【列索引】 -> 【列名】
     */
    private Map<Integer, String> index2HeadName;

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {

        if (context.readRowHolder().getRowIndex() == 0) {
            this.index2HeadName = data;
        } else {
            handleData(data, index2HeadName);
        }
    }

    private void handleData(Map<Integer, String> data, Map<Integer, String> index2HeadName) {
        // - excel根据列index去识别数据；
        // - 对象解析通过列名去做。
        DynamicColumnEntity dynamicColumnEntity = null;
        try {
            dynamicColumnEntity = this.cellMap2CustomWrappedEntityConvertor.cellMap2CustomWrappedEntity(
                    data, index2HeadName, DynamicColumnEntity.class
            );
        } catch (InvocationTargetException | InstantiationException
                | IllegalAccessException | NoSuchMethodException e) {
            log.error("map转指定的实体失败！exMessage = {}", e.getMessage());
        }

        log.info("dynamic column entity = {}", dynamicColumnEntity);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
    }

    @Autowired
    public void setCellMap2CustomWrappedEntityConvertor(
            CellMap2CustomWrappedEntityConvertor cellMap2CustomWrappedEntityConvertor) {
        this.cellMap2CustomWrappedEntityConvertor = cellMap2CustomWrappedEntityConvertor;
    }
}
