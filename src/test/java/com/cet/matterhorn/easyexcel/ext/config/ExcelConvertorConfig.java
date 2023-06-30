package com.cet.matterhorn.easyexcel.ext.config;

import com.cet.matterhorn.easyexcel.ext.domain.entity.DynamicColumnEntity;
import com.cet.matterhorn.easyexcel.ext.domain.service.convertor.CellMap2CustomWrappedEntityConvertor;
import com.cet.matterhorn.easyexcel.ext.domain.service.convertor.impl.CellMap2CustomWrappedEntityConvertorImpl;
import com.cet.matterhorn.easyexcel.ext.domain.entity.IOperationWithEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ExcelConvertorConfig {

    @Bean
    @SuppressWarnings("unchecked")
    public <T> Class<T> customWrappedEntityClass() {
        return (Class<T>) DynamicColumnEntity.class;
    }

    @Bean
    public <T> IOperationWithEntity<T> entityOperation() {
        return entity -> log.info("dynamic column entity = {}", entity);
    }

    @Bean
    public CellMap2CustomWrappedEntityConvertor cellMap2CustomWrappedEntityConvertor() {
        return new CellMap2CustomWrappedEntityConvertorImpl();
    }
}
