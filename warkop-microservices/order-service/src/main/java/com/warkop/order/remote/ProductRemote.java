package com.warkop.order.remote;

import com.warkop.order.dto.order.ProductDTO;
import com.warkop.order.dto.order.ProductDataResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * [MICROSERVICES]
 * order-service TIDAK punya akses ke database produk.
 * Semua kebutuhan data produk dipenuhi lewat HTTP call ke product-service
 * (database-per-service pattern). Class ini setara dengan folder "remote"
 * pada project careops.
 */
@Slf4j
@Component
public class ProductRemote {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${product.service.url}")
    private String baseUrl;

    public ProductDTO getProduct(Integer productId) {
        try {
            ProductDataResponseDTO response = restTemplate.getForObject(
                    baseUrl + "/api/v1/product/detail?productId=" + productId,
                    ProductDataResponseDTO.class);

            return response == null ? null : response.getData();

        } catch (HttpClientErrorException e) {
            log.warn("[PRODUCT-REMOTE] Produk {} tidak ditemukan: {}", productId, e.getStatusCode());
            return null;
        } catch (RestClientException e) {
            log.error("[PRODUCT-REMOTE] Gagal menghubungi product-service: {}", e.getMessage());
            return null;
        }
    }

    public boolean decreaseStock(Integer productId, Integer qty) {
        try {
            restTemplate.postForEntity(
                    baseUrl + "/api/v1/product/decrease-stock?productId=" + productId + "&qty=" + qty,
                    null, String.class);
            return true;

        } catch (HttpClientErrorException e) {
            // product-service membalas 400 kalau stok tidak cukup
            return false;
        } catch (RestClientException e) {
            log.error("[PRODUCT-REMOTE] Gagal menghubungi product-service: {}", e.getMessage());
            return false;
        }
    }
}
