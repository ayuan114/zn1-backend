package com.jizy.zn1backend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jizy.zn1backend.common.BaseResponse;
import com.jizy.zn1backend.common.MinioStorageService;
import com.jizy.zn1backend.exception.BusinessException;
import com.jizy.zn1backend.exception.ErrorCode;
import com.jizy.zn1backend.model.dto.ArticleCreateRequest;
import com.jizy.zn1backend.model.dto.BlogArticleDTO;
import com.jizy.zn1backend.model.dto.ContentBlockDTO;
import com.jizy.zn1backend.model.entity.Article;
import com.jizy.zn1backend.model.entity.BlogArticle;
import com.jizy.zn1backend.mapper.BlogArticleMapper;
import com.jizy.zn1backend.model.entity.ContentBlock;
import com.jizy.zn1backend.model.entity.ImageMeta;
import com.jizy.zn1backend.model.vo.ArticleResponse;
import com.jizy.zn1backend.model.vo.BlogArticleResponse;
import com.jizy.zn1backend.service.BlogArticleService;
import com.jizy.zn1backend.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Administrator
 * @description 针对表【blog_article(博客文章表)】的数据库操作Service实现
 * @createDate 2025-06-16 17:37:43
 */
@Service
@Slf4j
public class BlogArticleServiceImpl extends ServiceImpl<BlogArticleMapper, BlogArticle>
        implements BlogArticleService {

    @Resource
    private MinioStorageService minioStorageService;

    @Resource
    private BlogArticleMapper blogArticleMapper;

    @Override
    public BlogArticleResponse createBlogArticle(BlogArticleDTO request) {
        BlogArticle article = new BlogArticle();
        BeanUtil.copyProperties(request, article);
        boolean save = this.save(article);
        BlogArticleResponse response = new BlogArticleResponse();
        BeanUtil.copyProperties(request, response);
        if (save) {
            return response;
        } else {
            return null;
        }
    }

    // 处理图片上传
    @Override
    public String handleImageUpload(MultipartFile file) {
        try {
            // 方式2：处理base64图片上传
            //byte[] imageData = Base64.getDecoder().decode(imageBlock.getContent());
            String fileName = file.getOriginalFilename();
            // 生成唯一图片ID
            String imageId = DigestUtils.md5DigestAsHex(fileName.getBytes());
            // 存储路径格式：/articles/{articleId}/{imageId}.{ext}
            LocalDateTime now = LocalDateTime.now();
            String format = DateUtil.format(now, "yyyyMMddHHmm");
            String storagePath = String.format("blog/%s", format, imageId);
            byte[] bytes = file.getBytes();
            String string = minioStorageService.saveToMinioStorage(bytes, storagePath);
            if (StrUtil.isBlank(string)) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "上传失败");
            }
            String fileUrl = minioStorageService.getFileUrl(string);
            log.info("文件地址获取成功: {}", fileUrl);
            return fileUrl;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public InputStream filedownload(String fileName) {
        try {
            InputStream inputStream = minioStorageService.downloadFile(fileName);
            return inputStream;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<BlogArticle> queryBlogArticleTitle(BlogArticleDTO request) {
        List<BlogArticle> blogArticles = blogArticleMapper.selectAlltitle();
        return blogArticles;
    }

    @Override
    public BlogArticle queryArticleIdByDetail(long id) {
        BlogArticle blogArticle = this.getById(id);
        if (ObjectUtil.isEmpty(blogArticle)) {
            return null;
        }
        return blogArticle;
    }
}




