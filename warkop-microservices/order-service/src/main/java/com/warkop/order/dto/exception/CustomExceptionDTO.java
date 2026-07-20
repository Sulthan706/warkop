package com.warkop.order.dto.exception;

import lombok.Getter;

@Getter
public class CustomExceptionDTO extends Exception {

    private final String errorCode;

    public CustomExceptionDTO(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
