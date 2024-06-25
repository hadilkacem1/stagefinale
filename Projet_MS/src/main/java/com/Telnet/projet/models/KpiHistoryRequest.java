package com.Telnet.projet.models;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;



public class KpiHistoryRequest {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDateP;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDateP;

    private Integer value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDateP() {
        return startDateP;
    }

    public void setStartDateP(LocalDate startDateP) {
        this.startDateP = startDateP;
    }

    public LocalDate getEndDateP() {
        return endDateP;
    }

    public void setEndDateP(LocalDate endDateP) {
        this.endDateP = endDateP;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getKpiName() {
        return kpiName;
    }

    public void setKpiName(String kpiName) {
        this.kpiName = kpiName;
    }


    public Integer getKpiObjectif() {
        return kpiObjectif;
    }

    public void setKpiObjectif(Integer kpiObjectif) {
        this.kpiObjectif = kpiObjectif;
    }

    public Long getKpiId() {
        return kpiId;
    }

    public void setKpiId(Long kpiId) {
        this.kpiId = kpiId;
    }

    private String kpiName;


    private Integer kpiObjectif;

    private Long kpiId;
}
