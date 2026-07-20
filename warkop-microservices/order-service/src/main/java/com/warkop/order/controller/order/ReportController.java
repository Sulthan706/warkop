package com.warkop.order.controller.order;

import com.warkop.order.dto.order.DailyRevenueDTO;
import com.warkop.order.dto.order.LoyalCustomerDTO;
import com.warkop.order.dto.order.OrderStatisticsDTO;
import com.warkop.order.dto.order.TopProductDTO;
import com.warkop.order.response.DataResponse;
import com.warkop.order.response.HandlerResponse;
import com.warkop.order.service.order.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/report", produces = {"application/json"})
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/statistics")
    public void getStatistics(HttpServletResponse response) throws IOException {

        DataResponse<OrderStatisticsDTO> data = new DataResponse<>();
        data.setData(reportService.getOrderStatistics());
        HandlerResponse.responseSuccessWithData(response, data);
    }

    @GetMapping("/daily-revenue")
    public void getDailyRevenue(HttpServletResponse response) throws IOException {

        DataResponse<List<DailyRevenueDTO>> data = new DataResponse<>();
        data.setData(reportService.getDailyRevenue());
        HandlerResponse.responseSuccessWithData(response, data);
    }

    @GetMapping("/top-products")
    public void getTopProducts(HttpServletResponse response, @RequestParam(defaultValue = "3") Integer limit) throws IOException {

        DataResponse<List<TopProductDTO>> data = new DataResponse<>();
        data.setData(reportService.getTopProducts(limit));
        HandlerResponse.responseSuccessWithData(response, data);
    }

    @GetMapping("/loyal-customers")
    public void getLoyalCustomers(HttpServletResponse response) throws IOException {

        DataResponse<List<LoyalCustomerDTO>> data = new DataResponse<>();
        data.setData(reportService.getLoyalCustomers());
        HandlerResponse.responseSuccessWithData(response, data);
    }
}
