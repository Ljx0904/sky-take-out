package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

/**
 * 分类业务接口
 */
public interface CategoryService {
    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult catPage(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 删除分类
     */
    void delete(Long id);

    /**
     * 新增分类
     * @param categoryDTO
     */
    void save(CategoryDTO categoryDTO);

    /**
     * 启用或禁用分类
     * @param id
     * @param status
     */
    void startOrStop(Long id, Integer status);

    /**
     * 修改分类
     * @param categoryDTO
     */
    void update(CategoryDTO categoryDTO);

    /**
     * 根据类型查询分类列表
     * @param type
     * @return
     */
    List<Category> typeList(Integer type);
}
