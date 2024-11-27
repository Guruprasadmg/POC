package com.poc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Data
public class DataSet {

    @Id
    @UuidGenerator
    private String id;
    @Column
    private Integer quarter;
    @Column
    private String stock;
    @Column
    private String date;
    @Column
    private String open;
    @Column
    private String high;
    @Column
    private String low;
    @Column
    private String close;
    @Column
    private Integer volume;
    @Column
    private String percent_change_price;
    @Column
    private String percent_change_volume_over_last_wk;
    @Column
    private Integer privious_weeks_volume;
    @Column
    private String next_weeks_open;
    @Column
    private String next_weeks_close;
    @Column
    private String percent_change_next_weeks_price;
    @Column
    private Integer days_to_next_dividend;
    @Column
    private String percent_return_next_dividend;
}
