package com.jizy.zn1backend.model.vo;

import com.jizy.zn1backend.model.enums.BlockType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
// 文章响应
public class ArticleResponse {
    private Long id;
    private String title;
    private Long authorId;
    private CategoryInfo category;
    private List<String> tags;
    private List<ContentBlockResponse> blocks;
    private LocalDateTime createTime;
    
    // 内容块响应
    public static class ContentBlockResponse {
        private BlockType type;
        private String content; // 文本内容或图片URL
        private String imageId;
        private String fileName; // 图片原始文件名
    }
    
    // 分类信息
    public static class CategoryInfo {
        private Long id;
        private String name;
    }
}