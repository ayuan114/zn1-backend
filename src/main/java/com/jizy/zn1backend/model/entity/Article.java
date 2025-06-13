package com.jizy.zn1backend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
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
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private String title;

    /**
     * 
     */
    private Long author_id;

    /**
     * 所属分类
     */
    private Long category_id;

    /**
     * 
     */
    private Object status;

    /**
     * 
     */
    private Date create_time;

    /**
     * 
     */
    private Date update_time;
}