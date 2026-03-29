package com.sky.controller.admin;


import com.sky.dto.CategoryPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
@RequestMapping("/admin/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    public Result<PageResult> catPage( CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分页查询分类信息:{}", categoryPageQueryDTO);
        PageResult pageResult= categoryService.catPage(categoryPageQueryDTO);
        return Result.success(pageResult);

    }
}
