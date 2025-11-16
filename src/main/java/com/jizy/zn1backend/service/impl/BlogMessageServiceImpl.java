package com.jizy.zn1backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jizy.zn1backend.model.entity.BlogMessage;
import com.jizy.zn1backend.mapper.BlogMessageMapper;
import com.jizy.zn1backend.service.BlogMessageService;
import org.springframework.stereotype.Service;

/**
* @author ji
* @description 针对表【blog_message(留言表)】的数据库操作Service实现
* @createDate 2025-11-16 14:54:11
*/
@Service
public class BlogMessageServiceImpl extends ServiceImpl<BlogMessageMapper, BlogMessage>
    implements BlogMessageService {

}




