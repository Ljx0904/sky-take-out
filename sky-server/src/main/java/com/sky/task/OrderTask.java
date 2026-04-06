package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrdersMapper ordersMapper;
    @Scheduled(cron = "0 * * * * ?")
    public void processingTimeoutOfOrders(){
        log.info("处理超时订单{}", LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        List<Orders> list =ordersMapper.getByTimeoutOfOrders(Orders.PAID,time);
        if(!CollectionUtils.isEmpty( list)){
            for (Orders orders : list) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("支付超时,自动取消");
                orders.setCancelTime(LocalDateTime.now());
                ordersMapper.update(orders);
            }
        }

    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void processingTimeoutOfDelivery(){
        log.info("处理派送超时订单{}", LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
        List<Orders> list =ordersMapper.getByTimeoutOfOrders(Orders.DELIVERY_IN_PROGRESS,time);
        if(!CollectionUtils.isEmpty( list)){
            for (Orders orders : list) {
                orders.setStatus(Orders.COMPLETED);
                orders.setDeliveryTime(LocalDateTime.now());
            }
        }
    }
}
