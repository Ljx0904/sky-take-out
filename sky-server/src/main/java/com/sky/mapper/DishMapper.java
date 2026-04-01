package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {
    /**
     * 插入菜品数据
     * @param dish
     */
    @AutoFill(value = OperationType.INSERT)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into dish (name, category_id, image,price, status, description, create_time, update_time, create_user, update_user)" +
            " VALUES (#{name}, #{categoryId},#{image}, #{price}, #{status},#{description}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void insert(Dish dish);


    /**
     * 分页查询
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> list(DishPageQueryDTO dishPageQueryDTO);



    /**
     * 修改菜品
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 根据id查询菜品和对对应的分类名字
     * @param id
     * @return
     */
    @Select("select d.* ,c.name as categoryName  from dish d left join category c on d.category_id=c.id where d.id=#{id}")
    DishVO getById(Long id);

    /**
     * 判断id集合菜品起售的数量
     * @param ids
     * @return
     */
    int countTheNumberOfSuspendedSales(List<Long> ids);


    /**
     * 批量删除菜品
     * @param ids
     */
    void deleteByIds(List<Long> ids);

    /**
     * 根据分类id查询菜品数量
     * @param id
     * @return*/
    @Select("select count(*) from dish where category_id=#{id}")
    Integer countByCategoryId(Long id);

    /**
     * 根据分类id查询菜品
     * @param categoryId
     */
    @Select("select id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user from dish  where category_id=#{categoryId} order by create_time desc")
    List<DishVO> selectByCategoryId(Long categoryId);
}
