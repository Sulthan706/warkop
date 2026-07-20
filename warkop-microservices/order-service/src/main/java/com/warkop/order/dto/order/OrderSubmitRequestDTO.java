package com.warkop.order.dto.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderSubmitRequestDTO {
    private String customerName;
    private Integer productId;
    private Integer quantity;
}
