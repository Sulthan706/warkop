package com.warkop.order.dto.order;

import lombok.Getter;
import lombok.Setter;

/**
 * Wrapper untuk membaca response product-service
 * yang formatnya { errorCode, errorMessage, data }.
 */
@Getter
@Setter
public class ProductDataResponseDTO {
    private String errorCode;
    private String errorMessage;
    private ProductDTO data;
}
