package com.jizy.zn1backend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import lombok.Data;

/**
 * 
 * @TableName article
 */
@TableName(value ="article")
@Data
public class Article {
    /**
     *
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     *
     */
    private String title;

    /**
     *
     */
    private Long authorId;

    /**
     * 所属分类
     */
    private Long categoryId;

    /**
     *
     */
    private Object status = ArticleStatus.DRAFT;

    /**
     *
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;


    private Set<Tag> tags = new HashSet<>();

    // 枚举定义
    public enum ArticleStatus {
        DRAFT, PUBLISHED, DELETED
    }
}