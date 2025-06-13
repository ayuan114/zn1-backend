package com.jizy.zn1backend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

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
    @TableId
    private Long article_id;

    /**
     * 
     */
    @TableId
    private Long tag_id;
}