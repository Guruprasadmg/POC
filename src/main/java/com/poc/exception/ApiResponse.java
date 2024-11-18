package com.poc.exception;

import lombok.Data;

@Data
public class ApiResponse {

    private String status;
    private String error;
}
