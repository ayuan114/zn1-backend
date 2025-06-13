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
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private Long article_id;

    /**
     * 
     */
    private Object block_type;

    /**
     * 
     */
    private String content;

    /**
     * 
     */
    private String image_id;

    /**
     * 
     */
    private Integer sort_order;
}