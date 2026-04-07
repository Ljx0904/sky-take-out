package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.*;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private DishMapper dishMapper;
    @Override
    public BusinessDataVO getBusinessData() {
        LocalDateTime begin = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime ent = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        Map map = new HashMap();
        map.put("begin",begin);
        map.put("end",ent);
        Integer totalOrderCount = ordersMapper.countMap(map);//订单总数
        Integer newUser = userMapper.getNewUser(map);//新增用户数
        map.put("status", Orders.COMPLETED);
        Integer validOrderCount = ordersMapper.countMap(map);//有效订单数
        Double turnoverStatistics = ordersMapper.getTurnoverStatistics(map);//营业额
        turnoverStatistics= turnoverStatistics==null? 0.0:turnoverStatistics;
        Double unitPrice = 0.0;

        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0 && validOrderCount != 0){
            //订单完成率
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
            //平均客单价
            unitPrice = turnoverStatistics / validOrderCount;
        }
        return BusinessDataVO.builder()
                .turnover(turnoverStatistics)
                .newUsers(newUser)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .validOrderCount(validOrderCount)
                .build();
    }

    /**
     * 订单概览
     * @return
     */
    @Override
    public OrderOverViewVO getOverviewOrders() {
        LocalDateTime begin = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime ent = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        Map map = new HashMap();
        map.put("begin",begin);
        map.put("end",ent);
        Integer counted = ordersMapper.countMap(map);
        map.put("status", Orders.TO_BE_CONFIRMED);
        Integer waitingOrders = ordersMapper.countMap(map);
        map.put("status", Orders.CONFIRMED);
        Integer deliveredOrders = ordersMapper.countMap(map);
        map.put("status", Orders.COMPLETED);
        Integer completedOrders = ordersMapper.countMap(map);
        map.put("status", Orders.CANCELLED);
        Integer cancelledOrders = ordersMapper.countMap(map);
        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .allOrders(counted)
                .build();
    }

    /**
     * 套餐总览
     * @return
     */
    @Override
    public SetmealOverViewVO getSetmealOverView() {
        Map map = new HashMap();
        map.put("status", StatusConstant.ENABLE);
        Integer sold = setmealMapper.countMap(map);
        map.put("status", StatusConstant.DISABLE);
        Integer discontinued = setmealMapper.countMap(map);
        return new SetmealOverViewVO(sold, discontinued);
    }

    /**
     * 菜品总览
     * @return
     */
    @Override
    public DishOverViewVO getDishOverViewVO() {
        Map map = new HashMap();
        map.put("status", StatusConstant.ENABLE);
        Integer sold = dishMapper.countMap(map);
        map.put("status", StatusConstant.DISABLE);
        Integer discontinued = dishMapper.countMap(map);


        return new DishOverViewVO(sold, discontinued);
    }
}
