package com.poc.model;

import lombok.Data;

import java.util.List;

@Data
public class ErrorResponse {
    private String FILENAME;
    private String ROW;
    private List<String> ERRORS;
}
