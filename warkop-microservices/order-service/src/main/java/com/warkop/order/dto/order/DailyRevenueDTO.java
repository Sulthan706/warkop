package com.warkop.order.dto.order;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DailyRevenueDTO {
    private String tanggal;
    private Long jumlahOrder;
    private Long totalItem;
    private BigDecimal totalPendapatan;
}
