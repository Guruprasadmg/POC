package com.poc.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.poc.entity.DataSet;
import com.poc.exception.DataSetException;
import com.poc.model.ClientResponse;
import com.poc.model.DataSetDTO;
import com.poc.repo.DataSetService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserDataSetServices {

    @Autowired
    DataSetService dataSetService;


    public ClientResponse bulkUpload(MultipartFile[] files) {
        List<DataSet> dataSets = new ArrayList<>();
        ClientResponse clientResponse = new ClientResponse();
        List<String> errors = new ArrayList<>();
        clientResponse.setStatus("Files are uploaded successfully");
        AtomicInteger failed = new AtomicInteger();
        AtomicInteger sucess = new AtomicInteger();
        if (ObjectUtils.isNotEmpty(files)) {
            clientResponse.setNoOfFilesSelected("Number of files are selected " + files.length);
            Arrays.stream(files).forEach(file -> {

                log.info(file.getOriginalFilename());
                if (file.getOriginalFilename().endsWith(".csv")) {
                    try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                        try (CSVReader csvReader = new CSVReader(reader)) {
                            List<String[]> data = csvReader.readAll();
                            data.stream().skip(1).forEach(stock -> {
                                DataSet dataSet = new DataSet();
                                dataSet.setQuarter(stock[0]);
                                dataSet.setStock(stock[1]);
                                dataSet.setDate(stock[2]);
                                dataSet.setOpen(stock[3]);
                                dataSet.setHigh(stock[4]);
                                dataSet.setLow(stock[5]);
                                dataSet.setClose(stock[6]);
                                dataSet.setVolume(stock[7]);
                                dataSet.setPercent_change_price(stock[8]);
                                dataSet.setPercent_change_volume_over_last_wk(stock[9]);
                                dataSet.setPrivious_weeks_volume(stock[10]);
                                dataSet.setNext_weeks_open(stock[11]);
                                dataSet.setNext_weeks_close(stock[12]);
                                dataSet.setPercent_change_next_weeks_price(stock[13]);
                                dataSet.setDays_to_next_dividend(stock[14]);
                                dataSet.setPercent_return_next_dividend(stock[15]);
                                dataSets.add(dataSet);
                            });
                        }
                    } catch (IOException | CsvException e) {
                        log.error("Failed to read the file {}", e.getMessage(), e);
                        throw new DataSetException(e.getMessage(), e);
                    }
                    sucess.addAndGet(1);

                } else {
                    errors.add("Failed to upload the file " + file.getOriginalFilename() + " since the invalid file format");
                    failed.addAndGet(1);

                }
            });
            dataSetService.saveAll(dataSets);
            log.info("Data {}", dataSets);
        } else {
            log.error("Please upload the valid file");
        }
        clientResponse.setUploaded("Number of files are uploaded " + (sucess.get()));
        clientResponse.setUploadFailed("Number of files are failed to upload " + (failed.get()));
        clientResponse.setErrors(errors);
        return clientResponse;
    }


    public ClientResponse getDetails(String stock) {
        try {
            ClientResponse clientResponse = new ClientResponse();
            List<DataSet> dataSets = dataSetService.getDataSetByStock(stock);
            List<DataSetDTO> dataSetDTOS = dataSets.stream().map(data -> {
                DataSetDTO dataSetDTO = new DataSetDTO();
                BeanUtils.copyProperties(data, dataSetDTO);
                return dataSetDTO;
            }).collect(Collectors.toList());
            if (ObjectUtils.isNotEmpty(dataSetDTOS)) {
                clientResponse.setDataSets(dataSetDTOS);
                return clientResponse;
            }
            clientResponse.setErrors(List.of("No data present"));
            return clientResponse;
        } catch (Exception exception) {
            log.error("Failed to fetch {}", exception.getMessage());
            throw new DataSetException(exception.getMessage(), exception);
        }
    }

    public ClientResponse add(DataSetDTO data) {
        ClientResponse clientResponse = new ClientResponse();
        try {
            DataSet dataSet = new DataSet();
            BeanUtils.copyProperties(data, dataSet);
            clientResponse.setStatus("Data has been added");
            return clientResponse;
        } catch (Exception exception) {
            log.error("Failed to add {}", exception.getMessage());
            throw new DataSetException(exception.getMessage(), exception);
        }
    }
}
