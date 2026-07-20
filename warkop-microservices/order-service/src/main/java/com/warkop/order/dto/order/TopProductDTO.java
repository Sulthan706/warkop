package com.warkop.order.dto.order;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TopProductDTO {
    private String productName;
    private Long totalTerjual;
    private BigDecimal totalPendapatan;
}
