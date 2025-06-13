package com.jizy.zn1backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jizy.zn1backend.model.entity.Tag;
import com.jizy.zn1backend.service.TagService;
import com.jizy.zn1backend.mapper.TagMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【tag】的数据库操作Service实现
* @createDate 2025-06-13 16:03:59
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




