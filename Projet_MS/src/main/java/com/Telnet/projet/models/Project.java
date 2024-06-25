package com.Telnet.projet.models;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "Projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String type;

    @Embedded
    private DateRangee projectDate;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "activity_id", referencedColumnName = "id")
    private Activity activity;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "ProjectsProcessus",
            joinColumns = @JoinColumn(name = "Projects_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "processus_id", referencedColumnName = "id")
    )
    private List<Processus> processus;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<KpiHistory> historyList = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    @JoinTable(name = "project_kpis", joinColumns = @JoinColumn(name = "project_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "kpi_id", referencedColumnName = "id"))
    private Set<Kpi> kpis;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "client_id")
    private Client cli;



    public Project() {
    }

    public Project(String name, String type, DateRangee projectDate, List<Processus> processus, Client cli, Set<Kpi> kpi,Activity activity) {
        this.name = name;
        this.type = type;
        this.projectDate = projectDate;
        this.processus = processus;
        this.cli = cli;
        this.kpis = kpis != null ? kpis : Collections.emptySet();
        this.activity=activity;

    }

    public Project(String name, String type, DateRangee projectDate, Client cli, Set<Kpi> kpis) {
        this.name = name;
        this.type = type;
        this.projectDate = projectDate;
        this.cli = cli;
        this.kpis = kpis;
    }

    public void addProcessus(Processus processus) {
        if (this.processus == null) {
            this.processus = new ArrayList<>();
        }
        this.processus.add(processus);
    }

    public Long getClientId() {
        return cli != null ? cli.getId() : null;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DateRangee getProjectDate() {
        return projectDate;
    }

    public void setProjectDate(DateRangee projectDate) {
        this.projectDate = projectDate;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public List<Processus> getProcessus() {
        return processus;
    }

    public void setProcessus(List<Processus> processus) {
        this.processus = processus;
    }

    public List<KpiHistory> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(List<KpiHistory> historyList) {
        this.historyList = historyList;
    }

    public Set<Kpi> getKpis() {
        return kpis;
    }

    public void setKpis(Set<Kpi> kpis) {
        this.kpis = kpis;
    }

    public Client getCli() {
        return cli;
    }

    public void setCli(Client cli) {
        this.cli = cli;
    }
}
