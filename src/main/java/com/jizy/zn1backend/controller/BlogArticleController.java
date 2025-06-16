package com.jizy.zn1backend.controller;

import cn.hutool.core.util.StrUtil;
import com.jizy.zn1backend.common.BaseResponse;
import com.jizy.zn1backend.exception.BusinessException;
import com.jizy.zn1backend.exception.ErrorCode;
import com.jizy.zn1backend.model.dto.ArticleCreateRequest;
import com.jizy.zn1backend.model.dto.BlogArticleDTO;
import com.jizy.zn1backend.model.vo.ArticleResponse;
import com.jizy.zn1backend.model.vo.BlogArticleResponse;
import com.jizy.zn1backend.model.vo.ImageUploadResponse;
import com.jizy.zn1backend.service.ArticleService;
import com.jizy.zn1backend.service.BlogArticleService;
import com.jizy.zn1backend.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/blog/article")
@Slf4j
public class BlogArticleController {

    @Autowired
    private BlogArticleService blogArticleService;


    // 创建图文混排文章
    @PostMapping
    public ResponseEntity<BlogArticleResponse> createBlogArticle(
            @RequestBody BlogArticleDTO request) {
        BlogArticleResponse response = blogArticleService.createBlogArticle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 文件上传
    @PostMapping("/upload")
    public BaseResponse<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            log.info("上传文件: {}", file.getOriginalFilename());
            String filePath = blogArticleService.handleImageUpload(file);
            if (StrUtil.isBlank(filePath)) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "上传失败");
            }
            return ResultUtils.success(filePath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "上传失败");
        }
    }
}