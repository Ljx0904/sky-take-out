package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.WatchService;

@Slf4j
@RestController
@RequestMapping("/admin/workspace")
public class WorkspaceController {

    @Autowired
    private WorkspaceService  workspaceService;
    /**
     * 营业数据统计
     * @return
     */
    @GetMapping("/businessData")
    public Result<BusinessDataVO> getBusinessData(){
        log.info("查询营业数据");
        return Result.success(workspaceService.getBusinessData());
    }

    /**
     * 订单统计
     * @return
     */
    @GetMapping("/overviewOrders")
    public Result<OrderOverViewVO> getOverviewOrders(){
        log.info("查询订单统计数据");
        return Result.success(workspaceService.getOverviewOrders());

    }

    @GetMapping("/overviewSetmeals")
    public Result<SetmealOverViewVO> getSetmealOverView(){
        log.info("查询套餐统计数据");
        return Result.success(workspaceService.getSetmealOverView());
    }

    @GetMapping("/overviewDishes")
    public Result<DishOverViewVO> getDishOverViewVO(){
        log.info("查询菜品统计数据");
        return Result.success(workspaceService.getDishOverViewVO());
    }
}
