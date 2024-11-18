package com.poc.controller;

import com.poc.model.ClientResponse;
import com.poc.service.UserDataSetServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("")
public class DataSetController {

    @Autowired
    private UserDataSetServices userDataSetServices;


    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ClientResponse> upload(@RequestPart(value = "file", required = true) MultipartFile[] files) {
        return new ResponseEntity<>(userDataSetServices.bulkUpload(files), HttpStatus.OK);
    }

    @GetMapping(path = "/details")
    public ResponseEntity<ClientResponse> getDetails(@RequestParam(value = "stock", required = true) String stock) {
        return new ResponseEntity<>(userDataSetServices.getDetails(stock), HttpStatus.OK);
    }

}
