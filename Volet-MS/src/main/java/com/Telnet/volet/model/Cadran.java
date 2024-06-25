package com.Telnet.volet.model;


import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Table(name = "cadrans")
@Entity
public class Cadran {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // private String strength;

    // private String weakness;

    // private String opportunity;

    // private String threat;

    @NotBlank
    private String name;

    @Enumerated(EnumType.STRING)
    private EType type;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "volet_id")
    private Volet volet;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDateP;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDateP;

    public Cadran() {
    }

    // public Cadran(String strength, String weakness, String opportunity, String
    // threat, Volet volet) {
    // this.strength = strength;
    // this.weakness = weakness;
    // this.opportunity = opportunity;
    // this.threat = threat;
    // this.volet = volet;
    // }

    public Cadran(@NotBlank String name, EType type, Volet volet) {
        this.name = name;
        this.type = type;
        this.volet = volet;
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

    public EType getType() {
        return type;
    }

    public void setType(EType type) {
        this.type = type;
    }

    public Volet getVolet() {
        return volet;
    }

    public void setVolet(Volet volet) {
        this.volet = volet;
    }

    // public String getStrength() {
    // return strength;
    // }

    // public void setStrength(String strength) {
    // this.strength = strength;
    // }

    // public String getWeakness() {
    // return weakness;
    // }

    // public void setWeakness(String weakness) {
    // this.weakness = weakness;
    // }

    // public String getOpportunity() {
    // return opportunity;
    // }

    // public void setOpportunity(String opportunity) {
    // this.opportunity = opportunity;
    // }

    // public String getThreat() {
    // return threat;
    // }

    // public void setThreat(String threat) {
    // this.threat = threat;
    // }

}
