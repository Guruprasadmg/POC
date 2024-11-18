package com.poc.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientResponse {

    private String status;
    private String noOfFilesSelected;
    private String uploaded;
    private String uploadFailed;
    private List<String> errors;
    private List<DataSetDTO> dataSets;
}
