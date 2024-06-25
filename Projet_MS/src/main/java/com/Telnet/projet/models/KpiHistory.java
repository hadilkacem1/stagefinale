package com.Telnet.projet.models;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Optional;

@Entity
@Table(name = "kpi_history")
public class KpiHistory {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "kpi_id")
    private Kpi kpi;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "project_id")
    private Project project;


    @Column(name = "kpi_name")
    private String kpiName;


    @Column(name = "kpi_objectif")
    private Integer objectif;


    private Integer value;


    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDateP;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDateP;


    public KpiHistory() {
    }


    public KpiHistory(Kpi kpi, Project project, String kpiName, Integer objectif, Integer value, LocalDate startDateP,
                      LocalDate endDateP) {
        this.kpi = kpi;
        this.project = project;
        this.kpiName = kpiName;
        this.objectif = objectif;
        this.value = value;
        this.startDateP = startDateP;
        this.endDateP = endDateP;
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public Kpi getKpi() {
        return kpi;
    }

    public void setKpi(Kpi kpi) {
        this.kpi = kpi;
    }

    public Integer getValue() {
        return value;
    }


    public void setValue(Integer value) {
        this.value = value;
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


    public String getKpiName() {
        return kpiName;
    }


    public void setKpiName(String kpiName) {
        this.kpiName = kpiName;
    }


    public Project getProject() {
        return project;
    }


    public void setProject(Project project) {
        this.project = project;
    }

    public Integer getObjectif() {
        return objectif;
    }


    public void setObjectif(Integer objectif) {
        this.objectif = objectif;
    }


}