package com.jizy.zn1backend.controller;

import com.jizy.zn1backend.model.dto.ArticleCreateRequest;
import com.jizy.zn1backend.model.vo.ArticleResponse;
import com.jizy.zn1backend.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    
    @Autowired
    private ArticleService articleService;

    // 创建图文混排文章
    @PostMapping
    public ResponseEntity<ArticleResponse> createArticle(
            @RequestBody ArticleCreateRequest request) {
        ArticleResponse response = articleService.createArticle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 获取文章详情
    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponse> getArticle(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean includeContent) {
        ArticleResponse response = articleService.getArticle(id, includeContent);
        return ResponseEntity.ok(response);
    }

    // 上传图片接口
    @PostMapping("/{articleId}/images")
    public ResponseEntity<ImageUploadResponse> uploadImage(
            @PathVariable Long articleId,
            @RequestParam("file") MultipartFile file) {
        ImageUploadResponse response = articleService.uploadImage(articleId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    // 图片上传响应
    public static class ImageUploadResponse {
        private String imageId;
        private String url;
        private String placeholder; // {{IMAGE:img_id}}
    }
}