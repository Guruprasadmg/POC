package com.poc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandling {


    @ExceptionHandler(Exception.class)
    public ApiResponse exceptionHandling(Exception exception) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatus(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        apiResponse.setError(exception.getMessage());
        return apiResponse;
    }

    @ExceptionHandler(DataSetException.class)
    public ApiResponse exception(DataSetException exception) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatus(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        apiResponse.setError(exception.getMessage());
        return apiResponse;
    }
}
