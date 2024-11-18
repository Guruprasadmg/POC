package com.poc.model;

import lombok.Data;

@Data
public class DataSetDTO {
    private String quarter;

    private String stock;

    private String date;

    private String open;

    private String high;

    private String low;

    private String close;

    private String volume;

    private String percent_change_price;

    private String percent_change_volume_over_last_wk;

    private String privious_weeks_volume;

    private String next_weeks_open;

    private String next_weeks_close;

    private String percent_change_next_weeks_price;

    private String days_to_next_dividend;

    private String percent_return_next_dividend;
}
