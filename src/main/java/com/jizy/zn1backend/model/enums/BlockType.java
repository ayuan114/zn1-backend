package com.jizy.zn1backend.model.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

@Getter
public enum BlockType {

    TEXT("文本", "TEXT"),

    IMAGE("图片", "IMAGE");

    private final String text;

    private final String value;

    BlockType(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据value 获取枚举
     */
    public static BlockType getEnumByValue(String value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (BlockType blockType : BlockType.values()) {
            if (blockType.getValue().equals(value)) {
                return blockType;
            }
        }
        return null;
    }
}
