package com.jizy.zn1backend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 * @TableName category
 */
@TableName(value ="category")
@Data
public class Category {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private String name;

    /**
     * 父分类ID
     */
    private Long parent_id;

    /**
     * 分类层级
     */
    private Integer level;

    /**
     * 
     */
    private Integer sort_order;
}