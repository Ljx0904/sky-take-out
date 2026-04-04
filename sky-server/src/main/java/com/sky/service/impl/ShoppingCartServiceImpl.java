package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart=new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        //判断当前菜品或套餐是否在购物车中
        List<ShoppingCart> list=shoppingCartMapper.list(shoppingCart);

        if (list!=null&&list.size()>0){
            //如果已经存在，则数量加1并更新
            ShoppingCart sc = list.get(0);
            sc.setNumber(sc.getNumber()+1);
            shoppingCartMapper.update(sc);
        }else {
            //如果不存在，则添加到购物车

            //获取当前菜品或套餐的详细信息，向购物车表插入数据
            if (shoppingCart.getSetmealId() != null) {

                SetmealVO setmealVO = setmealMapper.selectById(shoppingCart.getSetmealId());
                shoppingCart.setName(setmealVO.getName());
                shoppingCart.setImage(setmealVO.getImage());
                shoppingCart.setAmount(setmealVO.getPrice());

            } else if (shoppingCart.getDishId() != null) {
                DishVO byId = dishMapper.getById(shoppingCart.getDishId());
                shoppingCart.setName(byId.getName());
                shoppingCart.setImage(byId.getImage());
                shoppingCart.setAmount(byId.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    /**
     * 查看购物车
     * @return
     */
    @Override
    public List<ShoppingCart> list() {
        ShoppingCart shoppingCart = ShoppingCart.builder().userId(BaseContext.getCurrentId()).build();
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        return list;
    }

    /**
     * 清空购物车
     */
    @Override
    public void clean() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteAll(userId);
    }

    /**
     * 删除购物车
     * @param shoppingCartDTO
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        ShoppingCart sc = list.get(0);
        Integer number = sc.getNumber();
        if (number==1){
            shoppingCartMapper.deleteById(sc.getId());
        }else {
            sc.setNumber(number-1);
            shoppingCartMapper.update(sc);
        }
    }
}
