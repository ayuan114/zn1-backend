package com.jizy.zn1backend.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jizy.zn1backend.common.MinioStorageService;
import com.jizy.zn1backend.mapper.*;
import com.jizy.zn1backend.model.dto.ArticleCreateRequest;
import com.jizy.zn1backend.model.dto.ContentBlockDTO;
import com.jizy.zn1backend.model.entity.*;
import com.jizy.zn1backend.model.enums.BlockType;
import com.jizy.zn1backend.model.vo.ArticleResponse;
import com.jizy.zn1backend.model.vo.ImageUploadResponse;
import com.jizy.zn1backend.service.*;
import com.jizy.zn1backend.utils.MinioUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【article】的数据库操作Service实现
 * @createDate 2025-06-13 15:57:41
 */
@Service
@Slf4j
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
        implements ArticleService {

    @Resource
    private CategoryService categoryService;
    @Resource
    private TagService tagService;
    @Resource
    private ArticleTagService articleTagService;
    @Resource
    private ImageMetaService imageMetaService;
    @Resource
    private ContentBlockService contentBlockService;
    @Resource
    private MinioStorageService minioStorageService;
    @Resource
    private MinioUtils minioUtils;

    // 创建图文混排文章
    public ArticleResponse createArticle(ArticleCreateRequest request) {
        // 1. 创建文章元数据
        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setAuthorId(request.getAuthorId());
        article.setStatus(Article.ArticleStatus.DRAFT);

        // 设置分类
        if (request.getCategoryId() != null) {
            Category category = categoryService.getById(request.getCategoryId());
            article.setCategoryId(category.getId());
        }

        // 设置标签
        for (String tagName : request.getTags()) {
            QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
            queryWrapper.like(StrUtil.isNotBlank(tagName), "name", tagName);
            Tag tag = tagService.getOne(queryWrapper);
            article.getTags().add(tag);
        }

        //文章内容保存
        this.save(article);

        //文章关联标签
        Set<Tag> articleTags = article.getTags();
        List<ArticleTag> tagList = new ArrayList<>();
        for (Tag tag : articleTags) {
            ArticleTag articleTag = new ArticleTag();
            articleTag.setArticleId(article.getId());
            articleTag.setTagId(tag.getId());
            tagList.add(articleTag);
        }
        articleTagService.saveBatch(tagList);

        // 2. 处理内容块
        List<ContentBlock> blocks = processContentBlocks(article, request.getBlocks());
        contentBlockService.saveBatch(blocks);

        // 3. 构建响应
        return buildArticleResponse(article, blocks);
    }

    // 处理内容块逻辑
    private List<ContentBlock> processContentBlocks(Article article, List<ContentBlockDTO> blockDTOs) {
        List<ContentBlock> blocks = new ArrayList<>();
        int order = 0;

        for (ContentBlockDTO dto : blockDTOs) {
            ContentBlock block = new ContentBlock();
            block.setArticleId(article.getId());
            block.setBlockType(dto.getType());
            block.setSortOrder(order++);

            if (dto.getType() == BlockType.TEXT) {
                block.setContent(dto.getContent());
            } else if (dto.getType() == BlockType.IMAGE) {
                // 处理图片上传
                ImageMeta imageMeta = handleImageUpload(article, dto);
                block.setImageId(imageMeta.getId());
                block.setContent("{{IMAGE:" + imageMeta.getId() + "}}");
            }

            blocks.add(block);
        }

        return blocks;
    }

    // 处理图片上传
    private ImageMeta handleImageUpload(Article article, ContentBlockDTO imageBlock) {
        ImageMeta meta = new ImageMeta();
        meta.setArticleId(article.getId());

        // 两种图片处理方式：
        if (imageBlock.getImageId() != null) {
            // 方式1：使用已上传的图片ID
            return imageMetaService.getById(imageBlock.getImageId());
        } else if (imageBlock.getContent() != null) {
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
            // 保存元数据
            meta.setId(imageId);
            meta.setArticleId(article.getId());
            meta.setFileName(fileName);
            meta.setStoragePath(fileUrl);
            meta.setUploadTime(new Date());
            boolean save = imageMetaService.save(meta);
            if (save) {
                System.out.println("文件已成功写入: " + fileName);
                log.info("文件已成功写入: " + fileName);
                return meta;
            } else {
                return null;
            }
        } else {
            throw new IllegalArgumentException("Invalid image block data");
        }
    }

    // 获取文章详情
    public ArticleResponse getArticle(Long id, boolean includeContent) {
        Article article = this.getById(id);
        if (ObjectUtil.isEmpty(article)) {
            return null;
        }
        QueryWrapper<ContentBlock> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        List<ContentBlock> blocks = includeContent ?
                contentBlockService.list(queryWrapper) :
                Collections.emptyList();

        // 预取图片URL
        Map<String, String> imageUrls = preloadImageUrls(blocks);

        return buildArticleResponse(article, blocks, imageUrls);
    }

    // 上传图片文件
    public ImageUploadResponse uploadImage(Long articleId, MultipartFile file) {
        Article article = this.getById(articleId);

        String fileName = "image_" + System.currentTimeMillis() + ".png";
        // 生成唯一图片ID
        String imageId = DigestUtils.md5DigestAsHex(fileName.getBytes());
        // 存储路径格式：/articles/{articleId}/{imageId}.{ext}
        String storagePath = String.format("articles/%d/%s.%s", article.getId(), imageId, fileName);
        minioUtils.upload(file, storagePath);

        String fileUrl = minioStorageService.getFileUrl(storagePath);
        log.info("文件地址获取成功: {}" + fileUrl);

        // 保存元数据
        ImageMeta meta = new ImageMeta();
        meta.setId(imageId);
        meta.setArticleId(article.getId());
        meta.setFileName(file.getOriginalFilename());
        meta.setStoragePath(fileUrl);
        meta.setUploadTime(new Date());
        imageMetaService.save(meta);

        // 构建响应
        ImageUploadResponse response = new ImageUploadResponse();
        response.setImageId(imageId);
        response.setUrl(fileUrl);
        response.setPlaceholder("{{IMAGE:" + imageId + "}}");
        return response;
    }

    // 预加载图片URL
    private Map<String, String> preloadImageUrls(List<ContentBlock> blocks) {
        Set<String> imageIds = blocks.stream()
                .filter(b -> b.getBlockType() == BlockType.IMAGE)
                .map(b -> b.getImageId())
                .collect(Collectors.toSet());

        Map<String, String> urlMap = new HashMap<>();
        for (String imageId : imageIds) {
            ImageMeta meta = imageMetaService.getById(imageId);
            if (meta != null) {
                urlMap.put(imageId, meta.getStoragePath());
            }
        }
        return urlMap;
    }

    // 构建响应对象
    private ArticleResponse buildArticleResponse(Article article, List<ContentBlock> blocks) {
        return buildArticleResponse(article, blocks, new HashMap<>());
    }

    private ArticleResponse buildArticleResponse(Article article, List<ContentBlock> blocks,
                                                 Map<String, String> imageUrls) {
        ArticleResponse response = new ArticleResponse();
        response.setId(article.getId());
        response.setTitle(article.getTitle());
        response.setAuthorId(article.getAuthorId());
        response.setCreateTime(article.getCreateTime());

        // 分类信息
        if (article.getCategoryId() != null) {
            ArticleResponse.CategoryInfo categoryInfo = new ArticleResponse.CategoryInfo();
            categoryInfo.setId(article.getCategoryId());
            Category category = categoryService.getById(article.getCategoryId());
            categoryInfo.setName(category.getName());
            response.setCategory(categoryInfo);
        }

        // 标签
        response.setTags(article.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toList()));

        // 内容块
        List<ArticleResponse.ContentBlockResponse> blockResponses = blocks.stream()
                .map(block -> convertBlock(block, imageUrls))
                .collect(Collectors.toList());
        response.setBlocks(blockResponses);

        return response;
    }

    private ArticleResponse.ContentBlockResponse convertBlock(ContentBlock block,
                                                              Map<String, String> imageUrls) {
        ArticleResponse.ContentBlockResponse response = new ArticleResponse.ContentBlockResponse();
        response.setType(block.getBlockType().toString());

        if (block.getBlockType() == BlockType.TEXT) {
            response.setContent(block.getContent());
        } else if (block.getBlockType() == BlockType.IMAGE) {
            String imageId = block.getImageId();
            ImageMeta meta = imageMetaService.getById(imageId);
            response.setImageId(meta.getId());
            response.setFileName(meta.getFileName());
            response.setContent(imageUrls.getOrDefault(meta.getId(), ""));
        }

        return response;
    }
}