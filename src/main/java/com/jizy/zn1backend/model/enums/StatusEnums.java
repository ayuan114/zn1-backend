package com.jizy.zn1backend.model.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

@Getter
public enum StatusEnums {

    DRAFT("草稿", "DRAFT"),

    PUBLISHED("发布", "PUBLISHED"),

    DELETED("删除", "DELETED");

    private final String text;

    private final String value;

    StatusEnums(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据value 获取枚举
     */
    public static StatusEnums getEnumByValue(String value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (StatusEnums blockType : StatusEnums.values()) {
            if (blockType.getValue().equals(value)) {
                return blockType;
            }
        }
        return null;
    }
}
