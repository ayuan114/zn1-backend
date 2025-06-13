package com.jizy.zn1backend.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jizy.zn1backend.mapper.ContentBlockMapper;
import com.jizy.zn1backend.mapper.ImageMetaMapper;
import com.jizy.zn1backend.model.dto.ArticleCreateRequest;
import com.jizy.zn1backend.model.dto.ContentBlockDTO;
import com.jizy.zn1backend.model.entity.*;
import com.jizy.zn1backend.model.enums.BlockType;
import com.jizy.zn1backend.model.vo.ArticleResponse;
import com.jizy.zn1backend.service.ArticleService;
import com.jizy.zn1backend.mapper.ArticleMapper;
import com.jizy.zn1backend.service.CategoryService;
import com.jizy.zn1backend.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

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
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
    implements ArticleService {
    @Autowired
    private ArticleMapper articleRepo;

    @Autowired
    private ContentBlockMapper blockRepo;

    @Autowired
    private ImageMetaMapper imageMetaRepo;

    @Autowired
    private StorageService storageService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TagService tagService;

    // 创建图文混排文章
    public ArticleResponse createArticle(ArticleCreateRequest request) {
        // 1. 创建文章元数据
        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setAuthor_id(request.getAuthorId());
        article.setStatus(0);

        // 设置分类
        if (request.getCategoryId() != null) {
            Category category = categoryService.getById(request.getCategoryId());
            article.setCategory_id(category.getId());
        }

        // 设置标签
        for (String tagName : request.getTags()) {
            QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
            queryWrapper.like(StrUtil.isNotBlank(tagName), "name", tagName);
            Tag tag = tagService.getOne(queryWrapper);
            article.getTags().add(tag);
        }

        Boolean aBoolean = this.save(article);

        // 2. 处理内容块
        List<ContentBlock> blocks = processContentBlocks(article, request.getBlocks());
        blockRepo.saveAll(blocks);

        // 3. 构建响应
        return buildArticleResponse(article, blocks);
    }

    // 处理内容块逻辑
    private List<ContentBlock> processContentBlocks(Article article, List<ContentBlockDTO> blockDTOs) {
        List<ContentBlock> blocks = new ArrayList<>();
        int order = 0;

        for (ContentBlockDTO dto : blockDTOs) {
            ContentBlock block = new ContentBlock();
            block.setArticle_id(article.getCategory_id());
            block.setBlock_type(dto.getType());
            block.setSort_order(order++);

            if (dto.getType() == BlockType.TEXT) {
                block.setContent(dto.getContent());
            }
            else if (dto.getType() == BlockType.IMAGE) {
                // 处理图片上传
                ImageMeta imageMeta = handleImageUpload(article, dto);
                block.setImage_id(imageMeta);
                block.setContent("{{IMAGE:" + imageMeta.getId() + "}}");
            }

            blocks.add(block);
        }

        return blocks;
    }

    // 处理图片上传
    private ImageMeta handleImageUpload(Article article, ContentBlockDTO imageBlock) {
        ImageMeta meta = new ImageMeta();
        meta.setArticle(article);

        // 两种图片处理方式：
        if (imageBlock.getImageId() != null) {
            // 方式1：使用已上传的图片ID
            return imageMetaRepo.findById(imageBlock.getImageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Image not found"));
        }
        else if (imageBlock.getContent() != null) {
            // 方式2：处理base64图片上传
            byte[] imageData = Base64.getDecoder().decode(imageBlock.getContent());
            String fileName = "image_" + System.currentTimeMillis() + ".png";
            return uploadImageData(article, imageData, fileName);
        }
        else {
            throw new IllegalArgumentException("Invalid image block data");
        }
    }

    // 获取文章详情
    public ArticleResponse getArticle(Long id, boolean includeContent) {
        Article article = articleRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        List<ContentBlock> blocks = includeContent ?
                blockRepo.findByArticleIdOrderBySortOrder(id) :
                Collections.emptyList();

        // 预取图片URL
        Map<String, String> imageUrls = preloadImageUrls(blocks);

        return buildArticleResponse(article, blocks, imageUrls);
    }

    // 上传图片文件
    public ImageUploadResponse uploadImage(Long articleId, MultipartFile file) {
        Article article = articleRepo.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        try {
            // 生成唯一图片ID
            String imageId = DigestUtils.md5DigestAsHex(file.getBytes());

            // 存储路径格式：/articles/{articleId}/{imageId}.{ext}
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            String storagePath = String.format("articles/%d/%s.%s", articleId, imageId, ext);

            // 上传到对象存储
            storageService.upload(storagePath, file.getInputStream());

            // 保存元数据
            ImageMeta meta = new ImageMeta();
            meta.setId(imageId);
            meta.setArticle(article);
            meta.setFileName(file.getOriginalFilename());
            meta.setStoragePath(storagePath);
            meta.setUploadTime(LocalDateTime.now());
            imageMetaRepo.save(meta);

            // 构建响应
            ImageUploadResponse response = new ImageUploadResponse();
            response.setImageId(imageId);
            response.setUrl(storageService.generateUrl(storagePath));
            response.setPlaceholder("{{IMAGE:" + imageId + "}}");
            return response;
        } catch (IOException e) {
            throw new StorageException("Failed to upload image", e);
        }
    }

    // 预加载图片URL
    private Map<String, String> preloadImageUrls(List<ContentBlock> blocks) {
        Set<String> imageIds = blocks.stream()
                .filter(b -> b.getBlockType() == BlockType.IMAGE)
                .map(b -> b.getImageMeta().getId())
                .collect(Collectors.toSet());

        Map<String, String> urlMap = new HashMap<>();
        for (String imageId : imageIds) {
            ImageMeta meta = imageMetaRepo.findById(imageId).orElse(null);
            if (meta != null) {
                urlMap.put(imageId, storageService.generateUrl(meta.getStoragePath()));
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
        if (article.getCategory() != null) {
            ArticleResponse.CategoryInfo categoryInfo = new ArticleResponse.CategoryInfo();
            categoryInfo.setId(article.getCategory().getId());
            categoryInfo.setName(article.getCategory().getName());
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
        response.setType(block.getBlockType());

        if (block.getBlockType() == BlockType.TEXT) {
            response.setContent(block.getContent());
        } else if (block.getBlockType() == BlockType.IMAGE) {
            ImageMeta meta = block.getImageMeta();
            response.setImageId(meta.getId());
            response.setFileName(meta.getFileName());
            response.setContent(imageUrls.getOrDefault(meta.getId(), ""));
        }

        return response;
    }
}




