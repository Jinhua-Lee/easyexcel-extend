package com.jinhua.easyexcel.ext.service;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

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
     * @param response Http响应对象，用于输出excel文件
     */
    void dynamicHeadOut(HttpServletResponse response);

    /**
     * 复杂头导入
     *
     * @param inputStream 输入流
     */
    void complexHeadIn(InputStream inputStream);
}
