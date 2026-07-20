package com.warkop.order.controller.order;

import com.warkop.order.dto.exception.CustomExceptionDTO;
import com.warkop.order.dto.order.OrderSubmitRequestDTO;
import com.warkop.order.model.order.OrderModel;
import com.warkop.order.response.DataResponse;
import com.warkop.order.response.HandlerResponse;
import com.warkop.order.service.order.OrderService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/order", produces = {"application/json"})
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/list")
    public void getAllOrder(HttpServletResponse response) throws IOException {

        DataResponse<List<OrderModel>> data = new DataResponse<>();
        data.setData(orderService.getAllOrder());
        HandlerResponse.responseSuccessWithData(response, data);
    }

    @GetMapping("/status")
    public void getOrderByStatus(HttpServletResponse response, @RequestParam String status) throws IOException {

        DataResponse<List<OrderModel>> data = new DataResponse<>();
        data.setData(orderService.getOrderByStatus(status));
        HandlerResponse.responseSuccessWithData(response, data);
    }

    @PostMapping("/submit")
    public void submitOrder(HttpServletResponse response, @RequestBody OrderSubmitRequestDTO request) throws IOException, CustomExceptionDTO {

        orderService.submitOrder(request);
        HandlerResponse.responseSuccessOK(response, "Order berhasil dibuat");
    }

    @PutMapping("/update-status")
    public void updateStatus(HttpServletResponse response,
                             @RequestParam Integer orderId,
                             @RequestParam String status) throws IOException, CustomExceptionDTO {

        orderService.updateStatus(orderId, status);
        HandlerResponse.responseSuccessOK(response, "Status order berhasil diubah");
    }
}
