package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> timeList=new ArrayList();
        List<Integer> validOrderCountList=new ArrayList<>();
        List<Integer> orderCountList=new ArrayList<>();
        Map maps=new HashMap();
        maps.put("begin", LocalDateTime.of(begin, LocalTime.MIN));
        maps.put("end", LocalDateTime.of(end, LocalTime.MAX));
        Integer totalOrderCount = ordersMapper.countMap(maps);
        maps.put("status", Orders.COMPLETED);
        Integer validOrderCount = ordersMapper.countMap(maps);

        while (!begin.isAfter( end)){
            timeList.add(begin);
            LocalDateTime beginTime=LocalDateTime.of(begin, LocalTime.MIN);
            LocalDateTime endTime=LocalDateTime.of(begin, LocalTime.MAX);
            Map map=new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            Integer totalCount=ordersMapper.countMap(map);
            map.put("status", Orders.COMPLETED);
            Integer validCount=ordersMapper.countMap(map);

            validOrderCountList.add(validCount);
            orderCountList.add(totalCount);


            begin = begin.plusDays(1);
        }
        return OrderReportVO.builder()
                .dateList(StringUtils.join(timeList,","))
                .orderCountList(StringUtils.join(orderCountList,","))
                .validOrderCountList(StringUtils.join(validOrderCountList,","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate( validOrderCount.doubleValue()/totalOrderCount)
                .build();
    }

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {

        //创建时间集合
        List<LocalDate> time=new ArrayList();
        //创建金额集合
        List<Double> totalAmountList=new ArrayList<>();
        //循环时间集合
        while (!begin.isAfter( end)){
            time.add(begin);
            LocalDateTime beginTime=LocalDateTime.of(begin, LocalTime.MIN);
            LocalDateTime endTime=LocalDateTime.of(begin, LocalTime.MAX);
            Map map=new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double totalAmount =ordersMapper.getTurnoverStatistics(map);
            totalAmount = totalAmount == null ? 0.0 : totalAmount;
            totalAmountList.add(totalAmount);
            begin = begin.plusDays(1);
        }
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(time,","))
                .turnoverList(StringUtils.join(totalAmountList,","))
                .build();
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> timeList=new ArrayList();
        List<Integer> newUserList=new ArrayList<>();
        List<Integer> totalUserList=new ArrayList<>();

        while (!begin.isAfter( end)){
            //添加时间到集合
            timeList.add(begin);
            LocalDateTime beginTime=LocalDateTime.of(begin, LocalTime.MIN);
            LocalDateTime endTime=LocalDateTime.of(begin, LocalTime.MAX);
            Map map=new HashMap();

            map.put("end", endTime);
            //获取总用户数
            Integer totalUser=userMapper.getNewUser(map);
            totalUserList.add(totalUser);
            map.put("begin", beginTime);
            //获取新增用户数
            Integer newUser=userMapper.getNewUser(map);
            newUserList.add(newUser);

            begin = begin.plusDays(1);
        }
        return new UserReportVO(StringUtils.join(timeList,","),
                StringUtils.join(totalUserList,","),
                StringUtils.join(newUserList,",")
                );
    }

    /**
     * 销量排名
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getDishTop10(LocalDate begin, LocalDate end) {
       LocalDateTime beginTime=LocalDateTime.of(begin, LocalTime.MIN);
       LocalDateTime endTime=LocalDateTime.of(end, LocalTime.MAX);
       Map map=new HashMap();
       map.put("begin", beginTime);
       map.put("end", endTime);
       map.put("status", Orders.COMPLETED);
       List<Map<String, Object>> dishList=ordersMapper.getTop10(map);
       List<String> nameList=new ArrayList();
       List<String> numberList=new ArrayList();
       for (Map<String, Object> dish : dishList) {
           nameList.add((String) dish.get("name"));
           numberList.add(dish.get("number").toString());
       }

        return new SalesTop10ReportVO(StringUtils.join(nameList,","),
                StringUtils.join(numberList,",")
                );
    }
}
