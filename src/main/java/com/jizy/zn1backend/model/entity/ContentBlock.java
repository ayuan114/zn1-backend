package com.jizy.zn1backend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 * @TableName content_block
 */
@TableName(value ="content_block")
@Data
public class ContentBlock {
    /**
     * 
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 
     */
    private Long articleId;

    /**
     * 
     */
    private Object blockType;

    /**
     * 
     */
    private String content;

    /**
     * 
     */
    private String imageId;

    /**
     * 
     */
    private Integer sortOrder;
}