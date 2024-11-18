package com.poc.model;

import lombok.Data;

import java.util.List;

@Data
public class ClientResponse {

    private String status;
    private String noOfFilesSelected;
    private String uploaded;
    private String uploadFailed;
    private List<String> errors;
}
