package com.warkop.product.config;

import com.warkop.product.dto.exception.CustomExceptionDTO;
import com.warkop.product.response.DataResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomExceptionDTO.class)
    public ResponseEntity<DataResponse<Object>> handleCustomException(CustomExceptionDTO e) {
        DataResponse<Object> data = new DataResponse<>();
        data.setErrorCode(e.getErrorCode());
        data.setErrorMessage(e.getMessage());
        return ResponseEntity.badRequest().body(data);
    }
}
