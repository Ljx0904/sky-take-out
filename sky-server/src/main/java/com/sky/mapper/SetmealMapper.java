package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SetmealMapper {
    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    public Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);



    @Options(useGeneratedKeys = true, keyProperty = "id")
    @AutoFill(value = OperationType.INSERT)
    @Insert("insert into setmeal (name, category_id, price, status, " +
            "description, image, create_time, update_time, create_user, update_user) " +
            "values (#{name}, #{categoryId}, #{price}, #{status}, " +
            "#{description}, #{image}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void insert(Setmeal setmeal);

    @Select("select s.*,c.name as categoryName from setmeal s left join category c on s.category_id=c.id where s.id=#{id}")
    SetmealVO selectById(Long id);


    @AutoFill(value = OperationType.UPDATE)
    void update(Setmeal setmeal);

    void deleteByIds(List<Long> ids);
}
