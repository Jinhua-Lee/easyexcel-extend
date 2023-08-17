package com.cet.matterhorn.easyexcel.ext.domain.service.convertor;

import com.cet.matterhorn.easyexcel.ext.domain.valobj.meta.out.DynamicMetaAndDataToWrite;
import org.springframework.lang.Nullable;

import java.util.Collection;

/**
 * @author Jinhua-Lee
 */
public interface CustomWrappedEntity2DynamicMetaAndDataConvertor {


    /**
     * 通过动态对象，输出动态
     *
     * @param <T>                    动态对象类型
     * @param entities               动态对象实体，非empty
     * @param tClass                 实体的实现类型
     * @param autoIncrementNumIfNull 子对象动态列数目，如果自增类型为null时，默认生成【多少】组子对象的表头列，默认0组
     * @return 动态对象【元信息】【数据】
     */
    <T> DynamicMetaAndDataToWrite convert(Collection<T> entities, Class<T> tClass,
                                          @Nullable Integer autoIncrementNumIfNull);
}
