package com.jinhua.easyexcel.ext.config;

import com.jinhua.easyexcel.ext.domain.service.convertor.CellMap2CustomWrappedEntityConvertor;
import com.jinhua.easyexcel.ext.domain.service.convertor.impl.CellMap2CustomWrappedEntityConvertorImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExcelConvertorConfig {

    @Bean
    public CellMap2CustomWrappedEntityConvertor cellMap2CustomWrappedEntityConvertor() {
        return new CellMap2CustomWrappedEntityConvertorImpl();
    }
}
