package com.jinhua.easyexcel.ext.domain.entity.meta;

import com.jinhua.easyexcel.ext.domain.valobj.meta.ParentTypeAndFieldsVO;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 动态列分析类型，元数据
 *
 * @author Jinhua-Lee
 */
@Getter
@EqualsAndHashCode(of = "type")
public class DynamicColumnAnalysisInfo {

    private final Class<?> type;

    /**
     * 第一层对象，class信息 + fields信息 + 注解信息
     */
    private final ParentTypeAndFieldsVO parent;

    public DynamicColumnAnalysisInfo(Class<?> type) {
        this.type = type;
        this.parent = new ParentTypeAndFieldsVO(type);
    }

}
