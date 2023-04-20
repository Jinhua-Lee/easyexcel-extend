package com.jinhua.easyexcel.ext.service.impl;

import com.alibaba.excel.EasyExcel;
import com.jinhua.easyexcel.ext.service.StreamHandleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * @author Jinhua-Lee
 */
@Slf4j
@Service
public class StreamHandleServiceImpl implements StreamHandleService {

    private MapDataAnalysisEventListener mapDataAnalysisEventListener;

    @Override
    public void analyze(InputStream inputStream) {

        EasyExcel.read(inputStream, this.mapDataAnalysisEventListener).headRowNumber(0)
                .sheet("Sheet1").doRead();
    }

    @Autowired
    public void setMapDataAnalysisEventListener(MapDataAnalysisEventListener mapDataAnalysisEventListener) {
        this.mapDataAnalysisEventListener = mapDataAnalysisEventListener;
    }
}
