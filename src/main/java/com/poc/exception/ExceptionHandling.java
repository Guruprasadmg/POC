package com.poc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandling {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> exception(Exception exception) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatus(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        apiResponse.setError(exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }

    @ExceptionHandler(DataSetException.class)
    public ResponseEntity<ApiResponse> exception(DataSetException exception) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatus(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        apiResponse.setError(exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ApiResponse> exception(DataNotFoundException exception) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatus(String.valueOf(HttpStatus.NOT_FOUND.value()));
        apiResponse.setError(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatus(String.valueOf(HttpStatus.BAD_REQUEST.value()));
        apiResponse.setError(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }
}
