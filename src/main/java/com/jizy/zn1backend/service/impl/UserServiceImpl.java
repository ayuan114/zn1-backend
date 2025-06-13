package com.jizy.zn1backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jizy.zn1backend.model.entity.User;
import com.jizy.zn1backend.service.UserService;
import com.jizy.zn1backend.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2025-06-13 16:04:07
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




