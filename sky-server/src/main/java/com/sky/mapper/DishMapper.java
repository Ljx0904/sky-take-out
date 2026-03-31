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



    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    @Select("select d.* ,c.name as categoryName  from dish d left join category c on d.category_id=c.id where d.id=#{id}")
    DishVO getById(Long id);


    int countTheNumberOfSuspendedSales(List<Long> ids);


    void deleteByIds(List<Long> ids);

    @Select("select count(*) from dish where category_id=#{id}")
    Integer countByCategoryId(Long id);
}
