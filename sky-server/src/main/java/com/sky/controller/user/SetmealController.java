package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.script.ScriptContext;
import java.security.Security;
import java.util.List;

@Slf4j
@RestController("UserSetmealController")
@RequestMapping("//user/setmeal")
public class SetmealController {

    @Autowired
    public SetmealService setmealService;
    /**
     * 根据套餐id查询包含的菜品
     * @param id
     * @return
     */
    @GetMapping("/dish/{id}")
    public Result<List<DishItemVO>> dishListBySetmealId(@PathVariable Long id){
        log.info("查询套餐id为{}包含的菜品", id);
        List<DishItemVO> list=setmealService.dishListBySetmealId(id);
        return Result.success(list);
    }

    /**
     * 根据分类id查询套餐
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @Cacheable(cacheNames ="setmealCache",key = "#categoryId")//key: setmealCache::100
    public Result<List<Setmeal>> list(Long categoryId){
        log.info("根据分类id查询套餐：{}", categoryId);
        Setmeal setmeal=new Setmeal();
        setmeal.setCategoryId(categoryId);
        setmeal.setStatus(StatusConstant.ENABLE);
        List<Setmeal> list=setmealService.list(setmeal);
        return Result.success(list);


    }
}
