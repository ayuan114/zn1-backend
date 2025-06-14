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

    @Autowired
    private CategoryMapper categoryRepository;

    // 创建分类
    public Boolean createCategory(String name, Long parentId) {
        Category category = new Category();
        category.setName(name);

        if (parentId != null) {
            Category parent = this.getById(parentId);
            category.setParentId(parent.getId());
            category.setLevel(parent.getLevel() + 1);
        } else {
            category.setLevel(1);
        }

        return this.save(category);
    }

    // 获取分类树
    public List<Category> getCategoryTree() {
        List<Category> allCategories = this.getCategoryTree();
        Map<Long, Category> categoryMap = new HashMap<>();
        List<Category> roots = new ArrayList<>();

        // 第一遍：建立ID映射
        for (Category cat : allCategories) {
            categoryMap.put(cat.getId(), cat);
        }

        // 第二遍：构建树结构
        for (Category cat : allCategories) {
            if (cat.getParentId() == null) {
                roots.add(cat);
            } else {
                Category parent = categoryMap.get(cat.getParentId());
                if (parent != null) {
                    parent.getChildren().add(cat);
                }
            }
        }

        // 按sortOrder排序
        roots.sort(Comparator.comparingInt(Category::getSortOrder));
        return roots;
    }
}




