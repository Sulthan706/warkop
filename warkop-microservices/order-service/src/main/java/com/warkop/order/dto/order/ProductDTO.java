package com.warkop.order.dto.order;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductDTO {
    private Integer productId;
    private String productName;
    private String category;
    private BigDecimal price;
    private Integer stock;
}
