package com.jizy.zn1backend.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jizy.zn1backend.common.BaseResponse;
import com.jizy.zn1backend.exception.BusinessException;
import com.jizy.zn1backend.exception.ErrorCode;
import com.jizy.zn1backend.model.dto.ArticleCreateRequest;
import com.jizy.zn1backend.model.dto.BlogArticleDTO;
import com.jizy.zn1backend.model.entity.BlogArticle;
import com.jizy.zn1backend.model.entity.Category;
import com.jizy.zn1backend.model.entity.Tag;
import com.jizy.zn1backend.model.vo.ArticleResponse;
import com.jizy.zn1backend.model.vo.BlogArticleResponse;
import com.jizy.zn1backend.model.vo.ImageUploadResponse;
import com.jizy.zn1backend.service.ArticleService;
import com.jizy.zn1backend.service.BlogArticleService;
import com.jizy.zn1backend.service.CategoryService;
import com.jizy.zn1backend.service.TagService;
import com.jizy.zn1backend.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/blog/article")
@Slf4j
public class BlogArticleController {

    @Resource
    private BlogArticleService blogArticleService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private TagService tagService;

    /**
     * 创建博客文章
     *
     * @param request
     * @return
     */
    @PostMapping("/create")
    public BaseResponse<BlogArticleResponse> createBlogArticle(
            @RequestBody BlogArticleDTO request) {
        BlogArticleResponse response = blogArticleService.createBlogArticle(request);
        if (ObjectUtil.isNotEmpty(response)) {
            return ResultUtils.success(response);
        }
        throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "查询失败");
    }

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
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

    /**
     * 文件下载
     *
     * @param fileName
     * @return
     */
    @GetMapping("/download/{fileName}")
    public ResponseEntity<InputStreamResource> download(@PathVariable String fileName) {
        try {
            InputStream inputStream = blogArticleService.filedownload(fileName);
            InputStreamResource resource = new InputStreamResource(inputStream);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * 获取博客文章标题
     *
     * @param request
     * @return
     */
    @PostMapping("/query/title")
    public BaseResponse<List<BlogArticle>> queryBlogArticleTitle(
            @RequestBody BlogArticleDTO request) {
        List<BlogArticle> response = blogArticleService.queryBlogArticleTitle(request);
        if (!response.isEmpty()) {
            return ResultUtils.success(response);
        }
        throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "查询失败");
    }

    /**
     * 获取博客文章内容
     *
     * @param
     * @return
     */
    @PostMapping("/query/{id}")
    public BaseResponse<BlogArticle> queryArticleIdByDetail(
            @PathVariable Long id) {
        BlogArticle response = blogArticleService.queryArticleIdByDetail(id);
        if (ObjectUtil.isNotEmpty(response)) {
            return ResultUtils.success(response);
        }
        throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "查询失败");
    }

    /**
     * 获取分类数据
     *
     * @param
     * @return
     */
    @PostMapping("/categorys")
    public BaseResponse<List<Category>> queryCategoryData() {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("name");
        List<Category> categories = categoryService.list(queryWrapper);
        if (!categories.isEmpty()) {
            return ResultUtils.success(categories);
        }
        throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "查询失败");
    }

    /**
     * 获取标签数据
     *
     * @param
     * @return
     */
    @PostMapping("/tags")
    public BaseResponse<List<Tag>> queryTagData() {
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNotNull("name");
        List<Tag> tags = tagService.list(queryWrapper);
        if (!tags.isEmpty()) {
            return ResultUtils.success(tags);
        }
        throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "查询失败");
    }
}