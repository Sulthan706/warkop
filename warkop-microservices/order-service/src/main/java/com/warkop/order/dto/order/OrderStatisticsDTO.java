package com.warkop.order.dto.order;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class OrderStatisticsDTO {
    private Integer totalOrder;
    private Map<String, Long> jumlahPerStatus;
    private BigDecimal totalPendapatan;
    private Map<String, BigDecimal> pendapatanPerProduk;
    private BiggestOrderDTO orderTerbesar;
    private BigDecimal rataRataItemPerOrder;
    private List<String> pelangganUnik;
    private Long jumlahOrderBesar;
    private Long jumlahOrderKecil;

    @Getter
    @Setter
    public static class BiggestOrderDTO {
        private String customerName;
        private String productName;
        private BigDecimal total;
    }
}
