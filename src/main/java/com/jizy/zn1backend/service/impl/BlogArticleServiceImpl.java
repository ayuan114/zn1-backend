package com.jizy.zn1backend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jizy.zn1backend.common.BaseResponse;
import com.jizy.zn1backend.common.MinioStorageService;
import com.jizy.zn1backend.exception.BusinessException;
import com.jizy.zn1backend.model.dto.ArticleCreateRequest;
import com.jizy.zn1backend.model.dto.BlogArticleDTO;
import com.jizy.zn1backend.model.dto.ContentBlockDTO;
import com.jizy.zn1backend.model.entity.Article;
import com.jizy.zn1backend.model.entity.BlogArticle;
import com.jizy.zn1backend.mapper.BlogArticleMapper;
import com.jizy.zn1backend.model.entity.ImageMeta;
import com.jizy.zn1backend.model.vo.ArticleResponse;
import com.jizy.zn1backend.model.vo.BlogArticleResponse;
import com.jizy.zn1backend.service.BlogArticleService;
import com.jizy.zn1backend.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.Base64;
import java.util.Date;

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
    private String handleImageUpload(Article article, String fileName) {
        ImageMeta meta = new ImageMeta();
        meta.setArticleId(article.getId());


        // 方式2：处理base64图片上传
        byte[] imageData = Base64.getDecoder().decode(imageBlock.getContent());
        String fileName = "image_" + System.currentTimeMillis() + ".png";
        // 生成唯一图片ID
        String imageId = DigestUtils.md5DigestAsHex(fileName.getBytes());
        // 存储路径格式：/articles/{articleId}/{imageId}.{ext}
        String storagePath = String.format("articles/%d/%s.%s", article.getId(), imageId, fileName);
        String string = minioStorageService.saveToMinioStorage(imageData, storagePath);
        if (StrUtil.isBlank(string)) {
            return null;
        }
        String fileUrl = minioStorageService.getFileUrl(storagePath);
        log.info("文件地址获取成功: {}" + fileUrl);
        return fileUrl;
    }
}




