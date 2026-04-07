package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

public interface WorkspaceService {
    /**
     * 查询营业数据
     * @return
     */
    BusinessDataVO getBusinessData();

    /**
     * 统计订单情况 OverView
     * @return
     */
    OrderOverViewVO getOverviewOrders();

    /**
     * 统计套餐情况 OverView
     * @return
     */
    SetmealOverViewVO getSetmealOverView();

    /**
     * 统计菜品情况 OverView
     * @return
     */
    DishOverViewVO getDishOverViewVO();
}
