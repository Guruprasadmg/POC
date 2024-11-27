package com.poc.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.poc.entity.DataSet;
import com.poc.exception.DataNotFoundException;
import com.poc.exception.DataSetException;
import com.poc.model.ClientResponse;
import com.poc.model.DataSetDTO;
import com.poc.model.ErrorResponse;
import com.poc.repo.DataSetService;
import com.poc.util.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.poc.util.AppUtils.addRowErrorDetails;

@Service
@Slf4j
public class UserDataSetServices {

    @Autowired
    DataSetService dataSetService;

    @Value("${error.file.upload}")
    private String errorFileUpload;


    public ClientResponse bulkUpload(MultipartFile[] files) {
        List<DataSet> dataSets = new ArrayList<>();
        ClientResponse clientResponse = new ClientResponse();
        List<String> errors = new ArrayList<>();
        List<ErrorResponse> errorResponses = new ArrayList<>();
        AtomicInteger validFiles=new AtomicInteger(0);
        AtomicInteger inValidFiles=new AtomicInteger(0);

        if (ObjectUtils.isNotEmpty(files)) {
            clientResponse.setNoOfFilesSelected("Number of files are selected " + files.length);
            Arrays.stream(files).forEach(file -> {
                log.info(file.getOriginalFilename());
                if (file.getOriginalFilename().endsWith(".csv")) {
                    try {
                        CSVReader reader = new CSVReaderBuilder(new InputStreamReader(file.getInputStream())).build();
                        try {
                            if (reader.readNext().length != 16) {
                                log.error("Please add all required headers");
                                addRowErrorDetails("", file, List.of("Headers are missing for " + file.getOriginalFilename()), errorResponses);
                                inValidFiles.addAndGet(1);
                                return;
                            }
                            int rowNumber = 2;
                            String[] row;
                            AtomicInteger successRows=new AtomicInteger(0);
                            AtomicInteger failureRows=new AtomicInteger(0);
                            while ((row = reader.readNext()) != null) {
                                List<String> rowErrors = new ArrayList<>();
                                DataSet dataSet = setDataSet(row, dataSets, rowErrors);
                                DataSet recordExist = dataSetService.getDataSetByQuarterAndStockAndDateAndVolume(dataSet.getQuarter(), dataSet.getStock(), dataSet.getDate(), dataSet.getVolume());
                                if (ObjectUtils.isEmpty(rowErrors) && ObjectUtils.isEmpty(recordExist)) {
                                    dataSets.add(dataSet);
                                    successRows.addAndGet(1);
                                } else if (recordExist != null) {
                                    addRowErrorDetails(String.valueOf(rowNumber), file, List.of("Duplicate data"), errorResponses);
                                    failureRows.addAndGet(1);
                                } else {
                                    addRowErrorDetails(String.valueOf(rowNumber), file, rowErrors, errorResponses);
                                    failureRows.addAndGet(1);
                                }
                                rowNumber++;
                            }
                            log.info("File name : {}",file.getOriginalFilename());
                            log.info("Valid Number of rows {}",successRows.get());
                            log.info("Invalid Number of rows {}",failureRows.get());
                        } catch (Exception e) {
                            log.error("Failed to read the rows {}", e.getMessage(), e);
                            inValidFiles.addAndGet(1);
                            addRowErrorDetails("", file, List.of("Failed to read the row data " + file.getOriginalFilename() + " exception : " + e.getMessage()), errorResponses);
                        }
                    } catch (IOException e) {
                        log.error("Failed to read the file {}", e.getMessage(), e);
                        inValidFiles.addAndGet(1);
                        addRowErrorDetails("", file, List.of("Failed to read the file {}", e.getMessage()), errorResponses);
                    }
                } else {
                    errors.add("Failed to upload the file " + file.getOriginalFilename() + " since the invalid file format");
                    inValidFiles.addAndGet(1);
                    addRowErrorDetails("", file, List.of("Failed to upload the file " + file.getOriginalFilename() + " since the invalid file format"), errorResponses);
                }
            });
            if (ObjectUtils.isNotEmpty(dataSets) && !errors.isEmpty()) {
                clientResponse.setStatus("Partially Uploaded , Please check details below");
            } else if (ObjectUtils.isEmpty(dataSets) && !errors.isEmpty()) {
                clientResponse.setStatus("failure");
            }
            dataSetService.saveAll(dataSets);
            log.info("Data {}", dataSets);
        } else {
            log.error("Please upload the valid file");
            inValidFiles.addAndGet(1);
            throw new DataSetException("Please upload the valid file");
        }
        clientResponse.setErrors(errors);

        log.info("Errors {}", errorResponses);
        clientResponse.setUploadFailed(String.valueOf(inValidFiles.get()));
        clientResponse.setUploadFailed(String.valueOf(validFiles.get()));
        log.info("Number of valid files uploaded : {}",validFiles.get());
        log.info("Number of invalid files uploaded : {}",inValidFiles.get());
        fileWriter(errorResponses, clientResponse);
        return clientResponse;
    }

    private DataSet setDataSet(String[] row, List<DataSet> dataSets, List<String> rowErrors) {
        DataSet dataSet = new DataSet();
        dataSet.setQuarter(AppUtils.validateIsNotEmptyAndNumeric(row[0], rowErrors, "Quarter"));
        dataSet.setStock(AppUtils.validateIsNotEmpty(row[1], rowErrors, "Stock"));
        dataSet.setDate(AppUtils.validateDate(row[2], rowErrors, "Date"));
        dataSet.setOpen(AppUtils.validateIsNotEmptyAndDecimal(row[3], rowErrors, "Open"));
        dataSet.setHigh(AppUtils.validateIsNotEmptyAndDecimal(row[4], rowErrors, "High"));
        dataSet.setLow(AppUtils.validateIsNotEmptyAndDecimal(row[5], rowErrors, "Low"));
        dataSet.setClose(AppUtils.validateIsNotEmptyAndDecimal(row[6], rowErrors, "Close"));
        dataSet.setVolume(AppUtils.validateIsNotEmptyAndNumeric(row[7], rowErrors, "Volume"));
        dataSet.setPercent_change_price(AppUtils.validateIsNotEmptyAndDecimal(row[8], rowErrors, "percent_change_price"));
        dataSet.setPercent_change_volume_over_last_wk(AppUtils.validateIsNotEmptyAndDecimal(row[9], rowErrors, "percent_change_volume_over_last_wk"));
        dataSet.setPrivious_weeks_volume(AppUtils.validateIsNotEmptyAndNumeric(row[10], rowErrors, "previous_weeks_volume"));
        dataSet.setNext_weeks_open(AppUtils.validateIsNotEmptyAndDecimal(row[11], rowErrors, "next_weeks_open"));
        dataSet.setNext_weeks_close(AppUtils.validateIsNotEmptyAndDecimal(row[12], rowErrors, "next_weeks_close"));
        dataSet.setPercent_change_next_weeks_price(AppUtils.validateIsNotEmptyAndDecimal(row[13], rowErrors, "percent_change_next_weeks_price"));
        dataSet.setDays_to_next_dividend(AppUtils.validateIsNotEmptyAndNumeric(row[14], rowErrors, "days_to_next_dividend"));
        dataSet.setPercent_return_next_dividend(AppUtils.validateIsNotEmptyAndDecimal(row[15], rowErrors, "percent_return_next_dividend"));
        return dataSet;
    }


    public ClientResponse getDetails(String stock) {
        ClientResponse clientResponse = new ClientResponse();
        try {
            List<DataSet> dataSets;
            if (stock.equalsIgnoreCase("all")) {
                dataSets = dataSetService.findAllAsList();
                List<DataSetDTO> dataSetDTOS = dataSets.stream().map(data -> {
                    DataSetDTO dataSetDTO = new DataSetDTO();
                    BeanUtils.copyProperties(data, dataSetDTO);
                    dataSetDTO.setQuarter(String.valueOf(data.getQuarter()));
                    dataSetDTO.setVolume(String.valueOf(data.getVolume()));
                    dataSetDTO.setPrivious_weeks_volume(String.valueOf(data.getPrivious_weeks_volume()));
                    dataSetDTO.setDays_to_next_dividend(String.valueOf(data.getDays_to_next_dividend()));


                    return dataSetDTO;
                }).collect(Collectors.toList());
                clientResponse.setDataSets(dataSetDTOS);
            } else {
                dataSets = dataSetService.getDataSetByStock(stock);
                List<DataSetDTO> dataSetDTOS = dataSets.stream().map(data -> {
                    DataSetDTO dataSetDTO = new DataSetDTO();
                    BeanUtils.copyProperties(data, dataSetDTO);
                    dataSetDTO.setQuarter(String.valueOf(data.getQuarter()));
                    dataSetDTO.setVolume(String.valueOf(data.getVolume()));
                    dataSetDTO.setPrivious_weeks_volume(String.valueOf(data.getPrivious_weeks_volume()));
                    dataSetDTO.setDays_to_next_dividend(String.valueOf(data.getDays_to_next_dividend()));
                    return dataSetDTO;
                }).collect(Collectors.toList());
                if (ObjectUtils.isNotEmpty(dataSetDTOS)) {
                    clientResponse.setDataSets(dataSetDTOS);
                    return clientResponse;
                }
                clientResponse.setErrors(List.of("No data present"));
                throw new DataNotFoundException("No data present");
            }
            return clientResponse;
        } catch (DataNotFoundException e) {
            throw new DataNotFoundException("No data present");
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
            dataSetService.save(dataSet);
            return clientResponse;
        } catch (Exception exception) {
            log.error("Failed to add {}", exception.getMessage());
            throw new DataSetException("Failed to add data " + exception.getMessage(), exception);
        }
    }

    private void fileWriter(List<ErrorResponse> errorResponses, ClientResponse clientResponse) {
        if (!errorResponses.isEmpty()) {
            UUID uuid = UUID.randomUUID();
            String fileName = errorFileUpload + uuid + ".csv";
            clientResponse.setErrorFilePath(fileName);
            try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
                String[] header = {"ROW", "ERRORS", "FILENAME"};
                writer.writeNext(header);
                errorResponses.forEach(errorResponse -> {
                    String[] data = {errorResponse.getROW(), errorResponse.getERRORS().stream().collect(Collectors.joining("|")), errorResponse.getFILENAME()};
                    writer.writeNext(data);
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            log.info("Error response to csv done");
        }
    }
}
