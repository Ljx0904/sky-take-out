package com.sky.controller.admin;


import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> catPage( CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分页查询分类信息:{}", categoryPageQueryDTO);
        PageResult pageResult= categoryService.catPage(categoryPageQueryDTO);
        return Result.success(pageResult);

    }

    /**
     * 删除分类
     * @param id
     * @return
     */
    @DeleteMapping
    public Result delete(Long id){
        log.info("删除分类：{}", id);
        categoryService.delete(id);
        return Result.success();

    }

    /**
     * 新增分类
     * @param categoryDTO
     * @return
     */
    @PostMapping
    public Result save(@RequestBody CategoryDTO categoryDTO){
        log.info("新增分类：{}", categoryDTO);
        categoryService.save(categoryDTO);
        return Result.success();

    }

    @PostMapping("status/{status}")
    public Result startOrStop(@PathVariable Integer status,Long id){
        log.info("启用或禁用分类：{}", id);
        categoryService.startOrStop(id, status);
        return Result.success();
    }

    @PutMapping
    public Result update(@RequestBody CategoryDTO categoryDTO){
        log.info("修改分类：{}", categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }
    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    @GetMapping("/list")
    public Result<List> typeList(Integer  type){
        log.info("根据类型查询分类：{}", type);
        List<Category> list=categoryService.typeList(type);
        return Result.success(list);
    }
}
