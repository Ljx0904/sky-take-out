package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    /**
     * 查询当前用户的购物车数据
     * @param shoppingCart
     * @return
     */
    public List<ShoppingCart> list(ShoppingCart shoppingCart) ;

    /**
     * 更新购物车数据，数量
     * @param sc
     */
    @Update("update shopping_cart set number=#{number} where id=#{id}")
    void update(ShoppingCart sc);


    /**
     * 插入购物车数据
     * @param shoppingCart
     */
    @Insert("insert into shopping_cart (name, image,user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time ) " +
                    "values (#{name}, #{image},#{userId}, #{dishId}, #{setmealId}, #{dishFlavor}, #{number}, #{amount}, #{createTime})"
    )
    void insert(ShoppingCart shoppingCart);



    /**
     * 删除购物车中的数据
     * @param userId
     */
    @Delete("delete from shopping_cart where user_id=#{userId}")
    void deleteAll(Long userId);


    @Select("select * from shopping_cart where id=#{id}")
    ShoppingCart getById(Long id);

    /**
     * 删除购物车中的数据
     * @param id
     */
    @Delete("delete from shopping_cart where id= #{id}")
    void deleteById(Long id);
}
