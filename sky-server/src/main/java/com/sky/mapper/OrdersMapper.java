package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrdersDetailQueryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrdersMapper {
    /**
     * 插入订单数据
     * @param orders
     */
    void insert(Orders orders);
    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 分页查询订单
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders>  pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查询订单详情
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    OrdersDetailQueryVO getById(Long id);

    /**
     * 各个状态的订单数量统计
     * @param status
     * @return
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

    /**
     * 查询指定状态的订单
     * @param status
     * @param time
     * @return
     */
    @Select("select * from orders where status=#{status} and order_time<#{time}")
    List<Orders> getByTimeoutOfOrders(Integer status, LocalDateTime time);


    /**
     * 统计指定时间区间内的营业额数据
     * @param map
     * @return
     */
    Double getTurnoverStatistics(Map map);

    /**
     * 统计指定时间区间内的订单数量
     * @param map
     * @return
     */
    Integer countMap(Map map);


    /**
     * 查询top10
     * @param map
     * @return
     */
    List<Map<String, Object>> getTop10(Map map);

}
