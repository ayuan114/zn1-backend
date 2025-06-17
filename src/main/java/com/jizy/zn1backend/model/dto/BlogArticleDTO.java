package com.jizy.zn1backend.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 博客文章表
 * @TableName blog_article
 */
@TableName(value ="blog_article")
@Data
public class BlogArticleDTO {

    /**
     * 文章标题
     */
    private String title;

    /**
     * 作者id
     */
    private Long author_id;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 标签集合
     */
    private String tags;

    /**
     * 所属分类 category -> id
     */
    private Long category_id;

    /**
     * 
     */
    private String status;

}