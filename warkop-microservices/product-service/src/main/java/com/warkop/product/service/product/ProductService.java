package com.warkop.product.service.product;

import com.warkop.product.dto.exception.CustomExceptionDTO;
import com.warkop.product.dto.product.LowStockReportDTO;
import com.warkop.product.dto.product.ProductCreateRequestDTO;
import com.warkop.product.dto.product.ProductSummaryDTO;
import com.warkop.product.model.product.ProductModel;
import com.warkop.product.repository.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<ProductModel> getAllProduct() {
        return productRepository.findAll();
    }

    public ProductModel getProductById(Integer productId) throws CustomExceptionDTO {
        return productRepository.findById(productId)
                .orElseThrow(() -> new CustomExceptionDTO("02", "Produk tidak ditemukan"));
    }

    public List<ProductModel> getProductByCategory(String category) {
        return productRepository.getProductByCategory(category.toUpperCase());
    }

    public List<ProductModel> searchProduct(String keyword) {
        return productRepository.searchByName(keyword);
    }

    public List<ProductModel> getPremiumProduct() {
        return productRepository.getAboveCategoryAverage();
    }

    // [MIDDLE - NATIVE SQL] mapping hasil Object[] dari GROUP BY ke DTO
    public List<ProductSummaryDTO> getSummaryPerCategory() {

        List<Object[]> results = productRepository.getSummaryPerCategory();

        return results.stream().map(row -> {

            ProductSummaryDTO dto = new ProductSummaryDTO();
            dto.setCategory((String) row[0]);
            dto.setTotalProduk(((Number) row[1]).longValue());
            dto.setHargaTermurah((BigDecimal) row[2]);
            dto.setHargaTermahal((BigDecimal) row[3]);
            dto.setHargaRataRata((BigDecimal) row[4]);
            dto.setTotalStok(((Number) row[5]).longValue());

            return dto;
        }).collect(Collectors.toList());
    }

    public ProductModel createProduct(ProductCreateRequestDTO request) throws CustomExceptionDTO {

        if (request.getProductName() == null || request.getProductName().isBlank()) {
            throw new CustomExceptionDTO("02", "Nama produk wajib diisi");
        }

        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new CustomExceptionDTO("02", "Harga produk tidak valid");
        }

        ProductModel product = new ProductModel();
        product.setProductName(request.getProductName());
        product.setCategory(request.getCategory() == null ? "LAINNYA" : request.getCategory().toUpperCase());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock() == null ? 0 : request.getStock());

        return productRepository.save(product);
    }

    @Transactional
    public boolean decreaseStock(Integer productId, Integer qty) {
        return productRepository.decreaseStock(productId, qty) > 0;
    }

    /**
     * [JAVA STREAM] groupingBy + mapping + sorted:
     * menu dikelompokkan per kategori, tiap item diformat jadi teks.
     */
    public Map<String, List<String>> getMenuGroupedByCategory() {

        return productRepository.findAll().stream()
                .sorted(Comparator.comparing(ProductModel::getPrice))
                .collect(Collectors.groupingBy(
                        ProductModel::getCategory,
                        LinkedHashMap::new,
                        Collectors.mapping(
                                p -> p.getProductName() + " (Rp" + p.getPrice().toPlainString() + ")",
                                Collectors.toList())));
    }

    /**
     * [JAVA STREAM] filter + sorted + mapToInt + sum:
     * laporan produk yang stoknya di bawah ambang batas.
     */
    public LowStockReportDTO getLowStockReport(Integer threshold) {

        List<ProductModel> lowStock = productRepository.findAll().stream()
                .filter(p -> p.getStock() < threshold)
                .sorted(Comparator.comparing(ProductModel::getStock))
                .collect(Collectors.toList());

        int totalUnit = lowStock.stream()
                .mapToInt(ProductModel::getStock)
                .sum();

        LowStockReportDTO dto = new LowStockReportDTO();
        dto.setThreshold(threshold);
        dto.setJumlahProduk(lowStock.size());
        dto.setTotalUnitTersisa(totalUnit);
        dto.setProduk(lowStock);

        return dto;
    }
}
