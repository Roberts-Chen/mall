package com.chentao.mall.service.impl;

import com.chentao.mall.consts.MallConst;
import com.chentao.mall.mapper.CategoryMapper;
import com.chentao.mall.pojo.Category;
import com.chentao.mall.service.ICategoryService;
import com.chentao.mall.vo.CategoryVo;
import com.chentao.mall.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements ICategoryService {
    @Autowired
    CategoryMapper categoryMapper;


    @Override
    public ResponseVo<List<CategoryVo>> selectAll() {
        List<Category> categories = categoryMapper.selectAll();
//        List<CategoryVo> categoryVos = new ArrayList<>();
//        for (Category category : categories) {
//            if (category.getParentId().equals(MallConst.ROOT_PARENT_ID)) {
//                categoryVos.add(category2CategoryVo(category));
//            }
//        }

        // lambda + stream
        List<CategoryVo> categoryVos = categories.stream()
                .filter(e -> e.getParentId().equals(MallConst.ROOT_PARENT_ID))
                .map(this::category2CategoryVo)
                .sorted(Comparator.comparing(CategoryVo::getSortOrder).reversed())
                .collect(Collectors.toList());

        // 根据夫类别查找子类别
        findSubCategory(categoryVos, categories);

        return ResponseVo.success(categoryVos);
    }


    /**
     * 查找子类别
     * @param categoryVos
     * @param categories
     */
    private void findSubCategory(List<CategoryVo> categoryVos, List<Category> categories) {
        for (CategoryVo categoryVo : categoryVos) {
            List<CategoryVo> subCategoryVos = new ArrayList<>();
            for (Category category : categories) {
                if (categoryVo.getId().equals(category.getParentId())) {
                    CategoryVo subCategoryVo = category2CategoryVo(category);
                    subCategoryVos.add(subCategoryVo);
                }
                // 将子类别降序排列
                subCategoryVos.sort(Comparator.comparing(CategoryVo::getSortOrder).reversed());

                // 继续递归查找子类别
                findSubCategory(subCategoryVos, categories);
            }
            // 将子类别添加到父类别下
            categoryVo.setSubCategories(subCategoryVos);
        }
    }

    /**
     * 将Category对象转成CategoryVo对象
     * @param category 需要转换的Category对象
     * @return 转换完成的CategoryVo对象
     */
    private CategoryVo category2CategoryVo(Category category) {
        CategoryVo categoryVo = new CategoryVo();
        BeanUtils.copyProperties(category, categoryVo);
        return categoryVo;
    }

    @Override
    public void findSubCategoryId(Integer id, Set<Integer> resultSet) {
        List<Category> categories = categoryMapper.selectAll();
        findSubCategoryId(id, resultSet, categories);
    }

    private void findSubCategoryId(Integer id, Set<Integer> resultSet, List<Category> categories) {
        for (Category category : categories) {
            if (category.getParentId().equals(id)) {
                resultSet.add(category.getId());
                findSubCategoryId(category.getId(), resultSet, categories);
            }
        }
    }
}
