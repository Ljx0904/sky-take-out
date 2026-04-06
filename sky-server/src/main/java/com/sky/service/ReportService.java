package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;

import java.time.LocalDate;

public interface ReportService {
    /**
     * 统计指定时间区间内的营业额数据
     * @param begin
     * @param end
     * @return
     */
    TurnoverReportVO getOrdersStatistics(LocalDate begin, LocalDate end);

    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);
}
