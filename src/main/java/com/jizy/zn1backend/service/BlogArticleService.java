package com.jizy.zn1backend.service;

import com.jizy.zn1backend.common.BaseResponse;
import com.jizy.zn1backend.model.dto.ArticleCreateRequest;
import com.jizy.zn1backend.model.dto.BlogArticleDTO;
import com.jizy.zn1backend.model.entity.BlogArticle;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jizy.zn1backend.model.vo.ArticleResponse;
import com.jizy.zn1backend.model.vo.BlogArticleResponse;

/**
* @author Administrator
* @description 针对表【blog_article(博客文章表)】的数据库操作Service
* @createDate 2025-06-16 17:37:43
*/
public interface BlogArticleService extends IService<BlogArticle> {

    BlogArticleResponse createBlogArticle(BlogArticleDTO request);

}
