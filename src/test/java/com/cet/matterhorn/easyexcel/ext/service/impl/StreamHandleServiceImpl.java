package com.cet.matterhorn.easyexcel.ext.service.impl;

import com.alibaba.excel.EasyExcel;
import com.cet.matterhorn.easyexcel.ext.domain.service.convertor.CustomWrappedEntity2DynamicMetaAndDataConvertor;
import com.cet.matterhorn.easyexcel.ext.domain.entity.DynamicColumnEntity;
import com.cet.matterhorn.easyexcel.ext.domain.service.listener.DefaultMap2CustomEntityListener;
import com.cet.matterhorn.easyexcel.ext.domain.valobj.meta.out.DynamicMetaAndDataToWrite;
import com.cet.matterhorn.easyexcel.ext.service.StreamHandleService;
import com.cet.matterhorn.easyexcel.ext.service.listener.ComplexDynamicEntityListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * @author Jinhua-Lee
 */
@Slf4j
@Service
public class StreamHandleServiceImpl implements StreamHandleService {

    private DefaultMap2CustomEntityListener<?> defaultMap2CustomEntityListener;
    private ComplexDynamicEntityListener<?> complexDynamicEntityListener;
    private CustomWrappedEntity2DynamicMetaAndDataConvertor customWrappedEntity2DynamicMetaAndDataConvertor;

    @Override
    public void dynamicHeadIn(InputStream inputStream) {

        EasyExcel.read(inputStream, this.defaultMap2CustomEntityListener).headRowNumber(0)
                .sheet("Sheet1").doRead();
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public void dynamicHeadOut(OutputStream outputStream) {
        DynamicMetaAndDataToWrite metaAndDataToWrite = this.customWrappedEntity2DynamicMetaAndDataConvertor
                .convert(buildList(), DynamicColumnEntity.class, 2);

        EasyExcel.write(outputStream)
                .head((List<List<String>>) metaAndDataToWrite.getMetaToWrite().getDynamicMeta())
                .needHead(true)
                .sheet("Sheet1")
                .doWrite(metaAndDataToWrite.getDataToWrite().getDynamicData());

    }

    private List<DynamicColumnEntity> buildList() {
        List<DynamicColumnEntity> dynamicColumnEntities = new ArrayList<>();
        DynamicColumnEntity dy1 = DynamicColumnEntity.builder()
                .name("ljh")
                .columnGatheredEntities(
                        new LinkedHashSet<>(
                                Arrays.asList(
                                        DynamicColumnEntity.ColumnGatheredEntity.builder()
                                                .a("ljh_a1")
                                                .b("ljh_b1")
                                                .subTypeIdentity("打开")
                                                .build(),
                                        DynamicColumnEntity.ColumnGatheredEntity.builder()
                                                .a("ljh_a2")
                                                .b("ljh_b2")
                                                .subTypeIdentity("闭合")
                                                .build()
                                )
                        )
                ).anotherColumnGatheredEntities(
                        Arrays.asList(
                                DynamicColumnEntity.AnotherColumnGatheredEntity.builder()
                                        .a("ljh_a1_s")
                                        .b("ljh_b1_s")
                                        .build(),
                                DynamicColumnEntity.AnotherColumnGatheredEntity.builder()
                                        .a("ljh_a2_s")
                                        .b("ljh_b2_s")
                                        .build()
                        )
                ).build();
        dy1.setId(1);
        DynamicColumnEntity dy2 = DynamicColumnEntity.builder()
                .name("lwk")
                .columnGatheredEntities(
                        Collections.singleton(
                                DynamicColumnEntity.ColumnGatheredEntity.builder()
                                        .a("lwk_a1")
                                        .b("lwk_b1")
                                        .subTypeIdentity("闭合")
                                        .build()
                        )
                ).anotherColumnGatheredEntities(
                        Arrays.asList(
                                DynamicColumnEntity.AnotherColumnGatheredEntity.builder()
                                        .a("lwk_a1_s")
                                        .b("lwk_b1_s")
                                        .build(),
                                DynamicColumnEntity.AnotherColumnGatheredEntity.builder()
                                        .a("lwk_a2_s")
                                        .b("lwk_b2_s")
                                        .build(),
                                DynamicColumnEntity.AnotherColumnGatheredEntity.builder()
                                        .a("lwk_a3_s")
                                        .b("lwk_b3_s")
                                        .build(),
                                DynamicColumnEntity.AnotherColumnGatheredEntity.builder()
                                        .a("lwk_a4_s")
                                        .b("lwk_b4_s")
                                        .build()
                        )
                ).build();
        dy2.setId(2);
        dynamicColumnEntities.add(dy1);
        dynamicColumnEntities.add(dy2);
        return dynamicColumnEntities;
    }

    @Override
    public void complexHeadIn(InputStream inputStream) {
        EasyExcel.read(inputStream, this.complexDynamicEntityListener).headRowNumber(1)
                .sheet("Sheet1").doRead();
    }

    @Autowired
    public void setDefaultMap2CustomEntityListener(DefaultMap2CustomEntityListener<?> defaultMap2CustomEntityListener) {
        this.defaultMap2CustomEntityListener = defaultMap2CustomEntityListener;
    }

    @Autowired
    public void setComplexDynamicEntityListener(ComplexDynamicEntityListener<?> complexDynamicEntityListener) {
        this.complexDynamicEntityListener = complexDynamicEntityListener;
    }

    @Autowired
    public void setCustomWrappedEntity2DynamicMetaAndDataConvertor(
            CustomWrappedEntity2DynamicMetaAndDataConvertor customWrappedEntity2DynamicMetaAndDataConvertor) {
        this.customWrappedEntity2DynamicMetaAndDataConvertor = customWrappedEntity2DynamicMetaAndDataConvertor;
    }

}
