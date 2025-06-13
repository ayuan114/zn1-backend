package com.jizy.zn1backend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName tag
 */
@TableName(value ="tag")
@Data
public class Tag {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标签名
     */
    private String name;

    /**
     * 
     */
    private Date create_time;
}