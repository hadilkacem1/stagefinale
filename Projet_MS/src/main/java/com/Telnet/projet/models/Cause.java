package com.Telnet.projet.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "cause")
public class Cause {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    public Cause(Long id, Analyse analyse, String nomCause) {
        this.id = id;
        this.analyse = analyse;
        this.nomCause = nomCause;
    }




    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "analyse_id")
    private Analyse analyse;


    private String nomCause;

    // Constructeurs, getters et setters
    private double pourcentage;




    public Cause() {
    }

    public Cause(Analyse analyse, String nomCause,double pourcentage) {
        this.analyse = analyse;
        this.nomCause = nomCause;
        this.pourcentage = pourcentage;
    }

    public double getPourcentage() {
        return pourcentage;
    }

    public void setPourcentage(double pourcentage) {
        this.pourcentage = pourcentage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Analyse getAnalyse() {
        return analyse;
    }

    public void setAnalyse(Analyse analyse) {
        this.analyse = analyse;
    }

    public String getNomCause() {
        return nomCause;
    }

    public void setNomCause(String nomCause) {
        this.nomCause = nomCause;
    }
}
