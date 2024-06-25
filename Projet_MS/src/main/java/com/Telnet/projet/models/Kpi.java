package com.Telnet.projet.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "kpis")
public class Kpi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(unique = true)
    private String name;

    public Set<Processus> getProcessusList() {
        return processusList;
    }

    public void setProcessusList(Set<Processus> processusList) {
        this.processusList = processusList;
    }


    @JsonIgnore
    @ManyToMany(mappedBy = "kpis")

    private Set<Processus> processusList = new HashSet<>();


    private Integer objectif;

    @Enumerated(EnumType.STRING)
    private FrequenceKPI frequence;


    @OneToMany(mappedBy = "kpi", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<KpiHistory> historyList = new ArrayList<>();

    @OneToMany(mappedBy = "kpi", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Analyse> analyses = new ArrayList<>();  // Ajoutez cette ligne pour la relation avec Analyse


    public Kpi() {
    }


    @JsonCreator
    public Kpi(@JsonProperty("name") String name) {
        this.name = name;
    }



    public Kpi(String name, Set<Processus> processusList, Set<Project> project, Integer objectif, FrequenceKPI frequence,
               List<KpiHistory> historyList) {
        this.name = name;
        this.processusList = processusList;
        this.objectif = objectif;
        this.frequence = frequence;
        this.historyList = historyList;
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


    public FrequenceKPI getFrequence() {
        return frequence;
    }

    public void setFrequence(FrequenceKPI frequence) {
        this.frequence = frequence;
    }

    public Integer getObjectif() {
        return objectif;
    }

    public void setObjectif(Integer objectif) {
        this.objectif = objectif;
    }

    public List<Analyse> getAnalyses() {
        return analyses;
    }

    public void setAnalyses(List<Analyse> analyses) {
        this.analyses = analyses;
    }

    public List<KpiHistory> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(List<KpiHistory> historyList) {
        this.historyList = historyList;
    }

    public void addHistory(KpiHistory history) {
        history.setKpi(this);
        historyList.add(history);
    }

    public void removeHistory(KpiHistory history) {
        historyList.remove(history);
        history.setKpi(null);
    }


}
