package com.jizy.zn1backend.model.dto;

import com.jizy.zn1backend.model.enums.BlockType;
import com.sun.istack.internal.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ArticleDTO {

    private String title;
    
    @NotNull
    private Long authorId;
    
    private Long categoryId;
    private List<String> tags = new ArrayList<>();
    private List<ContentBlockDTO> contentBlocks;
    
    // 嵌套内容块DTO
    public static class ContentBlockDTO {
        private BlockType type;
        private String content; // 文本内容或图片ID
    }
    
    // getters and setters
}