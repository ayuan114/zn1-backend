package com.jizy.zn1backend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jizy.zn1backend.common.MinioStorageService;
import com.jizy.zn1backend.exception.BusinessException;
import com.jizy.zn1backend.exception.ErrorCode;
import com.jizy.zn1backend.model.dto.BlogArticleDTO;
import com.jizy.zn1backend.model.entity.BlogArticle;
import com.jizy.zn1backend.mapper.BlogArticleMapper;
import com.jizy.zn1backend.model.vo.BlogArticleResponse;
import com.jizy.zn1backend.service.BlogArticleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;

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
        if (request.getId() != null) {
            BlogArticle article = new BlogArticle();
            article.setId(request.getId());
            article.setTags(request.getTags());
            article.setTitle(request.getTitle());
            article.setContent(request.getContent());
            article.setCategoryId(request.getCategoryId());
            article.setUpdateTime(new Date());
            boolean updatedById = this.updateById(article);
            if (updatedById) {
                return new BlogArticleResponse();
            }
            return null;
        } else {
            BlogArticle article = new BlogArticle();
            BeanUtil.copyProperties(request, article);
            // 生成10位随机长整型数
            //article.setId(RandomUtil.randomLong(1000000000L, 10000000000L));
            boolean save = this.save(article);
            BlogArticleResponse response = new BlogArticleResponse();
            BeanUtil.copyProperties(request, response);
            if (save) {
                return response;
            } else {
                return null;
            }
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
            String format = DateUtil.format(now, "yyyyMMdd");
            //String storagePath = String.format("blog/%s", format, imageId);
            String storagePath = String.format("blog/%s/%s.%s",
                    format,
                    imageId,
                    FilenameUtils.getExtension(fileName));
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

    @Override
    public QueryWrapper<BlogArticle> getQueryWrapper(BlogArticleDTO pictureQueryRequest) {
        QueryWrapper<BlogArticle> queryWrapper = new QueryWrapper<>();
        if (pictureQueryRequest == null) {
            return queryWrapper;
        }
        Long id = pictureQueryRequest.getId();
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);

        String title = pictureQueryRequest.getTitle();
        queryWrapper.like(StrUtil.isNotEmpty(title), "title", title);

        Long categoryId = pictureQueryRequest.getCategoryId();
        queryWrapper.eq(ObjUtil.isNotEmpty(categoryId), "category_id", categoryId);

        String tags = pictureQueryRequest.getTags();
        queryWrapper.like(StrUtil.isNotEmpty(tags), "tags", tags);
/*        List<String> stringList = new ArrayList<>();
        if (StringUtils.hasLength(tags)) {
            stringList = Arrays.stream(tags.split(",")).collect(Collectors.toList());
        }
        if (CollUtil.isNotEmpty(stringList)) {
            for (String tag : stringList) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }*/

        String sortOrder = pictureQueryRequest.getSortOrder();
        String sortField = pictureQueryRequest.getSortField();
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }
}




