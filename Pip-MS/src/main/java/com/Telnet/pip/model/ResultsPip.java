package com.Telnet.pip.model;

import com.Telnet.pip.model.Pip;
import com.Telnet.pip.model.Processus;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Table(name = "resultpips")
@Entity
public class ResultsPip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Attente
    @NotBlank
    private String expectation;

    @NotBlank
    private String risk;

    // Surveillance existante
    @NotBlank
    private String existantMonitoring;

    // Surveillance à mettre en place
    @NotBlank
    private String setupMonitoring;

    // PIP ID
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "pip_id")
    private Pip pip;


    @ManyToMany(cascade = CascadeType.DETACH)
    @JoinTable(name = "resultatpiProcessus", joinColumns = @JoinColumn(name = "resultatpip_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "processus_id", referencedColumnName = "id"))
    private List<Processus> processus;

    public ResultsPip() {
    }

    // public ResultsPip(@NotBlank String expectation, @NotBlank String risk, @NotBlank String impactedProcessus,
    //         @NotBlank String existantMonitoring, @NotBlank String setupMonitoring, List<Processus> processus, Pip pip) {
    //     this.expectation = expectation;
    //     this.risk = risk;
    //     this.existantMonitoring = existantMonitoring;
    //     this.setupMonitoring = setupMonitoring;
    //     this.processus = processus;
    //     this.pip = pip;
    // }

    // Ctor with pip id
    public ResultsPip(@NotBlank String expectation, @NotBlank String risk, @NotBlank String impactedProcessus,
                      @NotBlank String existantMonitoring, @NotBlank String setupMonitoring) {
        this.expectation = expectation;
        this.risk = risk;
        this.existantMonitoring = existantMonitoring;
        this.setupMonitoring = setupMonitoring;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExpectation() {
        return expectation;
    }

    public void setExpectation(String expectation) {
        this.expectation = expectation;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public String getExistantMonitoring() {
        return existantMonitoring;
    }

    public void setExistantMonitoring(String existantMonitoring) {
        this.existantMonitoring = existantMonitoring;
    }

    public String getSetupMonitoring() {
        return setupMonitoring;
    }

    public void setSetupMonitoring(String setupMonitoring) {
        this.setupMonitoring = setupMonitoring;
    }

    public Pip getPip() {
        return pip;
    }

    public void setPip(Pip pip) {
        this.pip = pip;
    }

    public List<Processus> getProcessus() {
        return processus;
    }

    public void setProcessus(List<Processus> processus) {
        this.processus = processus;
    }

}