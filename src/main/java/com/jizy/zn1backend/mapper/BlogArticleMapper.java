package com.jizy.zn1backend.mapper;

import com.jizy.zn1backend.model.entity.BlogArticle;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author Administrator
* @description 针对表【blog_article(博客文章表)】的数据库操作Mapper
* @createDate 2025-06-16 17:37:43
* @Entity com.jizy.zn1backend.model.entity.BlogArticle
*/
public interface BlogArticleMapper extends BaseMapper<BlogArticle> {
    List<BlogArticle> selectAlltitle();

}




