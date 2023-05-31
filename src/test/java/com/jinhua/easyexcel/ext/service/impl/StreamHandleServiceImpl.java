package com.jinhua.easyexcel.ext.service.impl;

import com.alibaba.excel.EasyExcel;
import com.jinhua.easyexcel.ext.service.StreamHandleService;
import com.jinhua.easyexcel.ext.domain.service.listener.DefaultMap2CustomEntityListener;
import com.jinhua.easyexcel.ext.service.listener.ComplexDynamicEntityListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * @author Jinhua-Lee
 */
@Slf4j
@Service
public class StreamHandleServiceImpl implements StreamHandleService {

    private DefaultMap2CustomEntityListener<?> defaultMap2CustomEntityListener;
    private ComplexDynamicEntityListener<?> complexDynamicEntityListener;

    @Override
    public void dynamicHeadIn(InputStream inputStream) {

        EasyExcel.read(inputStream, this.defaultMap2CustomEntityListener).headRowNumber(0)
                .sheet("Sheet1").doRead();
    }

    @Override
    public void dynamicHeadOut(HttpServletResponse response) {
    }

    @Override
    public void complexHeadIn(InputStream inputStream) {
        EasyExcel.read(inputStream, this.complexDynamicEntityListener).headRowNumber(1)
                .sheet("Sheet1").doRead();
    }

    @Autowired
    public void setMapDataAnalysisEventListener(
            DefaultMap2CustomEntityListener<?> defaultMap2CustomEntityListener) {
        this.defaultMap2CustomEntityListener = defaultMap2CustomEntityListener;
    }

    @Autowired
    public void setComplexDynamicEntityListener(ComplexDynamicEntityListener<?> complexDynamicEntityListener) {
        this.complexDynamicEntityListener = complexDynamicEntityListener;
    }
}
