package com.jizy.zn1backend.service;

import com.jizy.zn1backend.model.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【category】的数据库操作Service
* @createDate 2025-06-13 16:03:30
*/
public interface CategoryService extends IService<Category> {
    Boolean createCategory(String name, Long parentId);

    List<Category> getCategoryTree();
}
