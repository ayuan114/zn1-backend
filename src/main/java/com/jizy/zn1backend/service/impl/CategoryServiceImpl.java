package com.jizy.zn1backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jizy.zn1backend.model.entity.Category;
import com.jizy.zn1backend.service.CategoryService;
import com.jizy.zn1backend.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
* @author Administrator
* @description 针对表【category】的数据库操作Service实现
* @createDate 2025-06-13 16:03:30
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{
}




