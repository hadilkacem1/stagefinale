package com.Telnet.projet.models;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Date;

@Embeddable
public class DateRangee {
    @Column(name = "start_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private java.util.Date startDate;

    @Column(name = "end_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private java.util.Date endDate;

    public DateRangee() {}

    public DateRangee(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public java.util.Date getStartDate() {
        return startDate;
    }

    public void setStartDate(java.util.Date date) {
        this.startDate = date;
    }

    public java.util.Date getEndDate() {
        return endDate;
    }

    public void setEndDate(java.util.Date date) {
        this.endDate = date;
    }

    public int compareTo(Date endDate2) {
        // TODO Auto-generated method stub
        return 0;
    }
}
