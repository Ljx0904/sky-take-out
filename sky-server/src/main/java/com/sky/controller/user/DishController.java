package com.sky.controller.user;

import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController("UserDishController")
@RequestMapping("/user/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 菜品查询
     * @param categoryId
     * @return
     */
    @Cacheable(cacheNames = "dishCache",key = "#categoryId")
    @GetMapping("/list")
    public Result<List<DishVO>> list(Long categoryId){
        log.info("查询菜品：{}", categoryId);
        List<DishVO> dishList = dishService.dishListByCategoryId(categoryId);
        return Result.success(dishList);
    }
}
