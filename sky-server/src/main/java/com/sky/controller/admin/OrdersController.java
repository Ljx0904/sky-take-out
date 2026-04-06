package com.sky.controller.admin;


import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrdersService;
import com.sky.vo.OrderStatisticsVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/admin/order")
@RestController
@Slf4j
public class OrdersController {

    @Autowired
    private OrdersService ordersService;
    @GetMapping("/conditionSearch")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("条件搜索订单：{}", ordersPageQueryDTO);
        PageResult pageResult=ordersService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/details/{id}")
    public Result details(@PathVariable Long id){
        log.info("查询订单详情：{}", id);
        return Result.success(ordersService.getById(id));
    }
    @PutMapping("/cancel")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO){
        log.info("取消订单：{}", ordersCancelDTO);
        ordersService.cancelOrdersCancelDTO(ordersCancelDTO);
        return Result.success();
    }

    @PutMapping("/confirm")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        log.info("确认订单：{}", ordersConfirmDTO);
        ordersService.confirm(ordersConfirmDTO);
        return Result.success();

    }

    @PutMapping("/rejection")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO) throws Exception {
        log.info("订单拒绝：{}", ordersRejectionDTO);
        ordersService.reject(ordersRejectionDTO);
        return Result.success();
    }
    @PutMapping("/delivery/{id}")
    public Result delivery(@PathVariable Long id){
        log.info("订单派送：{}", id);
        ordersService.delivery(id);
        return Result.success();
    }

    /**
     * 完成订单
     *
     * @return
     */
    @PutMapping("/complete/{id}")
    public Result complete(@PathVariable("id") Long id) {
        ordersService.complete(id);
        return Result.success();
    }

    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> statistics(){
        log.info("订单统计");
        OrderStatisticsVO orderStatisticsVO=ordersService.statistics();
        return Result.success(orderStatisticsVO);


    }


}
