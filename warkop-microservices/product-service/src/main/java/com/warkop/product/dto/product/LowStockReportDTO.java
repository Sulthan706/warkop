package com.warkop.product.dto.product;

import com.warkop.product.model.product.ProductModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LowStockReportDTO {
    private Integer threshold;
    private Integer jumlahProduk;
    private Integer totalUnitTersisa;
    private List<ProductModel> produk;
}
