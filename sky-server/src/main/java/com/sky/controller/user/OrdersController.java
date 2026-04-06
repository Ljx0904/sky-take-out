package com.sky.controller.user;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrdersService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrdersDetailQueryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("UserOrdersController")
@RequestMapping("/user/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @PostMapping("/submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("用户下单：{}", ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO= ordersService.submit(ordersSubmitDTO);
        return Result.success(orderSubmitVO);


    }
    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")

    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = ordersService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    /**
     * 历史订单查询
     *
     * @return
     */
    @GetMapping("/historyOrders")
    public Result<PageResult> historyOrders(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("历史订单查询{}", ordersPageQueryDTO);
        PageResult pageResult=ordersService.historyOrders(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/orderDetail/{id}")
    public Result<OrdersDetailQueryVO> getById(@PathVariable Long id){
        log.info("查询订单详情，订单id为{}", id);
        OrdersDetailQueryVO ordersDetailQueryVO=ordersService.getById(id);
        return Result.success(ordersDetailQueryVO);
    }
    @PutMapping("/cancel/{id}")
    public Result cancel(@PathVariable Long id){
        log.info("取消订单：{}", id);
        ordersService.cancel(id);
        return Result.success();
    }


    /**
     * 再来一单
     *
     * @param id
     * @return
     */
    @PostMapping("/repetition/{id}")
    public Result repetition(@PathVariable Long id){
        log.info("再来一单：{}", id);
        ordersService.repetition(id);
        return Result.success();
    }


}
