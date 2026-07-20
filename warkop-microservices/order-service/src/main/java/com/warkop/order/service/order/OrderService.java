package com.warkop.order.service.order;

import com.warkop.order.dto.exception.CustomExceptionDTO;
import com.warkop.order.dto.order.OrderSubmitRequestDTO;
import com.warkop.order.dto.order.ProductDTO;
import com.warkop.order.model.order.OrderModel;
import com.warkop.order.remote.ProductRemote;
import com.warkop.order.repository.order.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class OrderService {

    // [SPRING IoC] dua dependency di-inject oleh container
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRemote productRemote;

    public List<OrderModel> getAllOrder() {
        return orderRepository.findAll();
    }

    public List<OrderModel> getOrderByStatus(String status) {
        return orderRepository.getOrderByStatus(status.toUpperCase());
    }

    public void submitOrder(OrderSubmitRequestDTO request) throws CustomExceptionDTO {

        if (request.getCustomerName() == null || request.getCustomerName().isBlank()) {
            throw new CustomExceptionDTO("02", "Nama customer wajib diisi");
        }

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new CustomExceptionDTO("02", "Quantity harus lebih dari 0");
        }

        ProductDTO product = productRemote.getProduct(request.getProductId());

        if (product == null) {
            throw new CustomExceptionDTO("02", "Produk tidak ditemukan");
        }

        boolean stockOk = productRemote.decreaseStock(request.getProductId(), request.getQuantity());

        if (!stockOk) {
            throw new CustomExceptionDTO("01", "Stok produk '" + product.getProductName() + "' tidak mencukupi");
        }

        OrderModel order = new OrderModel();
        order.setCustomerName(request.getCustomerName());
        order.setProductId(product.getProductId());
        order.setProductName(product.getProductName());
        order.setQuantity(request.getQuantity());
        order.setPrice(product.getPrice());
        order.setStatus("DIPROSES");
        order.setCreatedAt(LocalDateTime.now());

        orderRepository.save(order);
    }

    public void updateStatus(Integer orderId, String status) throws CustomExceptionDTO {

        OrderModel order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomExceptionDTO("02", "Order tidak ditemukan"));

        String newStatus = status.toUpperCase();

        if (!Arrays.asList("DIPROSES", "SELESAI", "BATAL").contains(newStatus)) {
            throw new CustomExceptionDTO("02", "Status tidak valid");
        }

        order.setStatus(newStatus);
        orderRepository.save(order);
    }
}
