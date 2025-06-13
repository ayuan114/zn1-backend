package com.jizy.zn1backend.service;

import com.jizy.zn1backend.model.dto.ArticleCreateRequest;
import com.jizy.zn1backend.model.entity.Article;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jizy.zn1backend.model.entity.ContentBlock;
import com.jizy.zn1backend.model.entity.ImageMeta;
import com.jizy.zn1backend.model.vo.ArticleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【article】的数据库操作Service
* @createDate 2025-06-13 15:57:41
*/
public interface ArticleService extends IService<Article> {

    ArticleResponse createArticle(ArticleCreateRequest request);

}
