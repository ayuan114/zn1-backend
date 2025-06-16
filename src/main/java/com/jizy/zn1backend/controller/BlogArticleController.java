package com.jizy.zn1backend.controller;

import com.jizy.zn1backend.model.dto.ArticleCreateRequest;
import com.jizy.zn1backend.model.vo.ArticleResponse;
import com.jizy.zn1backend.model.vo.ImageUploadResponse;
import com.jizy.zn1backend.service.ArticleService;
import com.jizy.zn1backend.service.BlogArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/blog/article")
public class BlogArticleController {
    
    @Autowired
    private ArticleService articleService;

    @Autowired
    private BlogArticleService blogArticleService;

    // 创建图文混排文章
    @PostMapping
    public ResponseEntity<ArticleResponse> createArticle(
            @RequestBody ArticleCreateRequest request) {
        ArticleResponse response = articleService.createArticle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}