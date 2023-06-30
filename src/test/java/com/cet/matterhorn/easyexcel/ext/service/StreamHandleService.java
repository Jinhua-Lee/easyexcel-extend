package com.cet.matterhorn.easyexcel.ext.service;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 流解析服务
 *
 * @author Jinhua-Lee
 */
public interface StreamHandleService {

    /**
     * 解析动态Head
     *
     * @param inputStream 输入流
     */
    void dynamicHeadIn(InputStream inputStream);

    /**
     * 将给定对象导出实体内容
     *
     * @param outputStream 输出流
     */
    void dynamicHeadOut(OutputStream outputStream);

    /**
     * 复杂头导入
     *
     * @param inputStream 输入流
     */
    void complexHeadIn(InputStream inputStream);
}
