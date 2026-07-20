package com.warkop.product.dto.product;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductSummaryDTO {
    private String category;
    private Long totalProduk;
    private BigDecimal hargaTermurah;
    private BigDecimal hargaTermahal;
    private BigDecimal hargaRataRata;
    private Long totalStok;
}
