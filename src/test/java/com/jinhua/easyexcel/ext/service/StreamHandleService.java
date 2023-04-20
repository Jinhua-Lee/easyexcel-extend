package com.jinhua.easyexcel.ext.service;

import java.io.InputStream;

/**
 * 流解析服务
 *
 * @author Jinhua-Lee
 */
public interface StreamHandleService {

    /**
     * 通过读取流，进行excel的解析，由于数据量原因暂无返回值
     *
     * @param inputStream 输入流
     */
    void analyze(InputStream inputStream);
}
