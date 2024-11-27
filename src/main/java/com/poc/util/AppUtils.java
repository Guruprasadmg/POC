package com.poc.util;

import com.poc.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.List;

@Slf4j
public class AppUtils {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

    public static String validateDate(String data, List<String> errors, String type) {
        try {
            sdf.parse(data);
            return data;
        } catch (Exception e) {
            errors.add("Please add valid " + type + " in this format MM/dd/yyyy");
            log.error("Failed to parse date {}", e.getMessage(), e);
            return "";
        }
    }

    public static String validateIsNotEmpty(String data, List<String> errors, String type) {
        if (StringUtils.isEmpty(data)) {
            errors.add(type + " can not be empty");
            return "";
        }
        return data;
    }

    public static Integer validateIsNotEmptyAndNumeric(String data, List<String> errors, String type) {
        if (ObjectUtils.isNotEmpty(data) && !StringUtils.isNumeric(data)) {
            errors.add("Please add valid numeric data for " + type);
            return 0;
        } else if (StringUtils.isEmpty(data)) {
            errors.add(type + " can not be empty");
            return 0;
        }
        return Integer.valueOf(data);
    }

    public static String validateIsNotEmptyAndDecimal(String data, List<String> errors, String type) {
        String number = data.replaceAll("\\$", "");
        if (ObjectUtils.isNotEmpty(data) && !NumberUtils.isParsable(number.trim())) {
            errors.add("Please add valid numeric or decimal for " + type);
            return "";
        } else if (StringUtils.isEmpty(data)) {
            errors.add(type + " can not be empty");
            return "";
        }
        return data;
    }

    public static void addRowErrorDetails(String rowNumber, MultipartFile file, List<String> rowErrors, List<ErrorResponse> errorResponses) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setROW(rowNumber);
        errorResponse.setFILENAME(file.getOriginalFilename());
        errorResponse.setERRORS(rowErrors);
        errorResponses.add(errorResponse);
    }

//    public static List<String> validate(DataSet dataSet) {
//        List<String> errors = new ArrayList<>();
//        if (ObjectUtils.isNotEmpty(dataSet.getQuarter()) && !StringUtils.isNumeric(dataSet.getQuarter())) {
//            errors.add("Please add valid quarter" + dataSet.getQuarter().);
//        } else {
//            errors.add("Quarter can not be empty");
//        }
//
//        if (ObjectUtils.isEmpty(dataSet.getStock())) {
//            errors.add("Stock can not be empty");
//        }
//
//        if (ObjectUtils.isNotEmpty(dataSet.getDate()) && AppUtils.dateValidate(dataSet.getDate())) {
//            errors.add("Please add valid date MM/dd/yyyy");
//        } else {
//            errors.add("Date can not be empty");
//        }
//
//        if (ObjectUtils.isNotEmpty(dataSet.getOpen()) && StringUtils.isNumeric(dataSet.getOpen())) {
//
//            errors.add("Please add valid open price");
//        } else {
//            errors.add("Open price can not be empty");
//        }
//
//        if (ObjectUtils.isNotEmpty(dataSet.getHigh()) && StringUtils.isNumeric(dataSet.getHigh())) {
//            errors.add("Please add valid high price");
//        } else {
//            errors.add("High price  can not be empty");
//        }
//
//        if (ObjectUtils.isNotEmpty(dataSet.getLow()) && StringUtils.isNumeric(dataSet.getLow())) {
//
//            errors.add("Please add valid low price");
//        } else {
//            errors.add("Low can not be empty");
//        }
//
//        if (ObjectUtils.isNotEmpty(dataSet.getClose()) && StringUtils.isNumeric(dataSet.getClose())) {
//            errors.add("Please add valid close price");
//        } else {
//            errors.add("Close price can not be empty");
//        }
//
//        if (ObjectUtils.isNotEmpty(dataSet.getVolume()) && StringUtils.isNumeric(dataSet.getVolume())) {
//
//            errors.add("Please add valid volume");
//        } else {
//            errors.add("volume can not be empty");
//        }
//
//        if (ObjectUtils.isNotEmpty(dataSet.getPercent_change_price()) && StringUtils.isNumeric(dataSet.getPercent_change_price())) {
//            errors.add("Please add valid percent change price");
//        } else {
//            errors.add("Percent change price can not be empty");
//        }
//
//        if (ObjectUtils.isNotEmpty(dataSet.getNext_weeks_open()) && StringUtils.isNumeric(dataSet.getNext_weeks_open())) {
//            errors.add("Please add valid next week open");
//        } else {
//            errors.add("Next week open can not be empty");
//        }
//
//        if (ObjectUtils.isNotEmpty(dataSet.getNext_weeks_close()) && StringUtils.isNumeric(dataSet.getNext_weeks_close())) {
//            errors.add("Please add valid next weeks close");
//        } else {
//            errors.add("Next weeks close can not be empty");
//        }
//
//        if (ObjectUtils.isNotEmpty(dataSet.getPercent_change_next_weeks_price()) && StringUtils.isNumeric(dataSet.getPercent_change_next_weeks_price())) {
//            errors.add("Please add valid percent change next week price");
//        } else {
//            errors.add("Percent change next week price can not be empty");
//        }
//
//        if (ObjectUtils.isNotEmpty(dataSet.getDays_to_next_dividend()) && StringUtils.isNumeric(dataSet.getDays_to_next_dividend())) {
//
//            errors.add("Please add valid days to next dividend");
//        } else {
//            errors.add("Days to next divided can not be empty");
//        }
//        if (ObjectUtils.isNotEmpty(dataSet.getPercent_return_next_dividend()) && StringUtils.isNumeric(dataSet.getPercent_return_next_dividend())) {
//
//            errors.add("Please add valid percent return next dividend");
//        } else {
//            errors.add("Percent return next divided can not be empty");
//        }
//        return errors;
//    }
}
