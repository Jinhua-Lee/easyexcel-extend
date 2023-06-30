package com.cet.matterhorn.easyexcel.ext.service.listener;

import com.cet.matterhorn.easyexcel.ext.domain.service.convertor.CellMap2CustomWrappedEntityConvertor;
import com.cet.matterhorn.easyexcel.ext.domain.service.listener.DefaultMap2CustomEntityListener;
import com.cet.matterhorn.easyexcel.ext.domain.entity.IOperationWithEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ComplexDynamicEntityListener<T> extends DefaultMap2CustomEntityListener<T> {
    public ComplexDynamicEntityListener(Class<T> customWrappedEntityClass,
                                        IOperationWithEntity<T> entityOperation,
                                        CellMap2CustomWrappedEntityConvertor cellMap2CustomWrappedEntityConvertor) {
        super(customWrappedEntityClass, entityOperation, cellMap2CustomWrappedEntityConvertor);
    }

    @Override
    protected int getHeadRowIndex() {
        // 加了复杂头，从第一行开始
        return 1;
    }
}
