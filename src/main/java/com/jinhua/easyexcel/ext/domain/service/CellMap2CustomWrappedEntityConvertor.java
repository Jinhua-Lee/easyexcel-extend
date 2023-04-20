package com.jinhua.easyexcel.ext.domain.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * 【Excel的CellMap】转换为自定义封装的转换器
 *
 * @author Jinhua-Lee
 */
public interface CellMap2CustomWrappedEntityConvertor {

    /**
     * 将【Excel的CellMap】，根据【列的头信息】，转换为指定封装对象的实例
     *
     * @param cellIndex2Data     数据Map：【列索引】-> 【列数据】
     * @param index2HeadName     列的头信息：【列索引】 -> 【列的头信息】
     * @param customWrappedClass 自定义封装的类型
     * @return 自定义封装的对象实体
     * @throws InvocationTargetException 调用异常
     * @throws InstantiationException    实例化异常
     * @throws IllegalAccessException    权限访问异常
     * @throws NoSuchMethodException     未找到类的方法异常
     */
    <T> T cellMap2CustomWrappedEntity(Map<Integer, String> cellIndex2Data,
                                      Map<Integer, String> index2HeadName, Class<T> customWrappedClass)
            throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException;
}
