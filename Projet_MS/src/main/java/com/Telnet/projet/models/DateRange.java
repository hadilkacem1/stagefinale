package com.Telnet.projet.models;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.sql.Date;

@Embeddable
public class DateRange {
    @Column(name = "start_date")
    @JsonFormat(pattern = "yyyy-MM")
    private Date startDate;

    @Column(name = "end_date")
    @JsonFormat(pattern = "yyyy-MM")
    private Date endDate;

    public DateRange() {}

    public DateRange(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
