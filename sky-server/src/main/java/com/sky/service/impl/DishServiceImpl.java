package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    /**
     * 新增菜品，同时插入菜品数据，需要操作两张表：dish、dish_flavor
     *
     * @param dishDTO
     */
    //开启事物
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(DishDTO dishDTO) {
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dish.setStatus(0);
        dishMapper.insert(dish);
        Long dishId = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (!CollectionUtils.isEmpty(flavors)){
            flavors.forEach(flavor->{
                flavor.setDishId(dishId);
            });
            dishFlavorMapper.insert(flavors);
        }


    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page =dishMapper.list(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());

    }


    /**
     * 批量起售停售
     * @param status
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Dish dish=Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.update(dish);

    }

    /**
     * 根据id查询菜品和对应的口味数据
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public DishVO getById(Long id) {
        DishVO dishVO = dishMapper.getById(id);
        List<DishFlavor> list = dishFlavorMapper.selectByDistId(id);
        if (!CollectionUtils.isEmpty(list)){
            dishVO.setFlavors(list);
        }
        return dishVO;
    }

    /**
     * 修改菜品
     * @param dishDTO
     */
    @Transactional
    @Override
    public void update(DishDTO dishDTO) {
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //修改菜品数据
        dishMapper.update(dish);
        Long dishId = dish.getId();
        //删除当前菜品对应的口味数据
        dishFlavorMapper.deleteByDishId(dishId);
        List<DishFlavor> flavors = dishDTO.getFlavors();

        if (!CollectionUtils.isEmpty(flavors)) {
            flavors.forEach(flavor->{
                flavor.setDishId(dishId);
            });
            dishFlavorMapper.insert(flavors);
        }
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Transactional
    @Override
    public void deleteByIds(List<Long> ids) {
        //判断多个id的停售数量
        int count =dishMapper.countTheNumberOfSuspendedSales(ids);
        //数量大于0时抛出异常
        if(count>0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }
        //判断是否有套餐包含次菜品
        List<Long> setmealIdByDistId = setmealDishMapper.getSetmealIdByDistId(ids);
        if (!CollectionUtils.isEmpty(setmealIdByDistId)){

            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除菜品
        dishMapper.deleteByIds(ids);
        //删除菜品对应的口味数据
        dishFlavorMapper.deleteByDishIds(ids);

    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @Override
    public List<DishVO> dishListByCategoryId(Long categoryId) {
        Dish dish=Dish.builder()
                .categoryId(categoryId)
                .status(1)
                .build();
        List<Dish> dishList = dishMapper.selectByCategoryId(dish);
        List<DishVO> dishVOList=new ArrayList<>();
        for (Dish d : dishList) {
            DishVO dishVO=new DishVO();
            BeanUtils.copyProperties(d, dishVO);
            List<DishFlavor> f = dishFlavorMapper.selectByDistId(d.getId());
            dishVO.setFlavors(f);
            dishVOList.add(dishVO);
        }
        return dishVOList;
    }
}
