package com.jizy.zn1backend.model.dto;

import com.jizy.zn1backend.model.enums.BlockType;
import lombok.Data;

@Data
// 图文混排内容块
public class ContentBlockDTO {
    private BlockType type; // TEXT, IMAGE
    private String content; // 文本内容或图片URL/base64
    private String imageId; // 仅图片块有效
    private int sortOrder;
}