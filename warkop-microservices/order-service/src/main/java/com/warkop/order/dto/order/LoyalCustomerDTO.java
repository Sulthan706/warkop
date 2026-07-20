package com.warkop.order.dto.order;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LoyalCustomerDTO {
    private String customerName;
    private Long jumlahOrder;
    private BigDecimal totalBelanja;
}
