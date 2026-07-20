package com.warkop.product.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataResponse<T> {
    private String errorCode;
    private String errorMessage;
    private T data;
}
