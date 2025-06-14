package com.jizy.zn1backend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName image_meta
 */
@TableName(value ="image_meta")
@Data
public class ImageMeta {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 
     */
    private Long articleId;

    /**
     * 
     */
    private String fileName;

    /**
     * 
     */
    private String storagePath;

    /**
     * 
     */
    private Date uploadTime;
}