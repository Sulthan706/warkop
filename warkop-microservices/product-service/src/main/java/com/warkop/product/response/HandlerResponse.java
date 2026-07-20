package com.warkop.product.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class HandlerResponse {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static void write(HttpServletResponse response, int status, Object body) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(MAPPER.writeValueAsString(body));
    }

    public static <T> void responseSuccessWithData(HttpServletResponse response, DataResponse<T> data) throws IOException {
        data.setErrorCode("00");
        data.setErrorMessage("Success");
        write(response, HttpServletResponse.SC_OK, data);
    }

    public static void responseSuccessOK(HttpServletResponse response, String message) throws IOException {
        DataResponse<Object> data = new DataResponse<>();
        data.setErrorCode("00");
        data.setErrorMessage(message);
        write(response, HttpServletResponse.SC_OK, data);
    }

    public static void responseBadRequest(HttpServletResponse response, String errorCode, String message) throws IOException {
        DataResponse<Object> data = new DataResponse<>();
        data.setErrorCode(errorCode);
        data.setErrorMessage(message);
        write(response, HttpServletResponse.SC_BAD_REQUEST, data);
    }
}
