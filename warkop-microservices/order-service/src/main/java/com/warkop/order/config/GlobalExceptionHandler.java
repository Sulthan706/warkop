package com.warkop.order.config;

import com.warkop.order.dto.exception.CustomExceptionDTO;
import com.warkop.order.response.DataResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Menangkap CustomExceptionDTO yang dilempar dari service,
 * sehingga controller cukup mendeklarasikan "throws CustomExceptionDTO"
 * tanpa perlu try-catch manual (pola yang sama dengan project careops).
 */
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
