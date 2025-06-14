package com.jizy.zn1backend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName article_tag
 */
@TableName(value ="article_tag")
@Data
public class ArticleTag {
    /**
     * 
     */
    private Long articleId;

    /**
     * 
     */
    private Long tagId;

    @Data
    // 复合主键类
    public static class ArticleTagId implements Serializable {
        private Long article;
        private Long tag;

        // equals and hashCode
    }
}