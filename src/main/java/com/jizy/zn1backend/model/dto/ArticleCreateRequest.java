package com.jizy.zn1backend.model.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
// 文章创建请求
public class ArticleCreateRequest {
    private String title;
    private Long authorId;
    private Long categoryId;
    private List<String> tags = new ArrayList<>();
    private List<ContentBlockDTO> blocks = new ArrayList<>();
}