package com.Telnet.projet.models;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "analyse")
public class Analyse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    public Kpi getKpi() {
        return kpi;
    }

    public void setKpi(Kpi kpi) {
        this.kpi = kpi;
    }

    @ManyToOne
    @JoinColumn(name = "kpi_id")
    private Kpi kpi;  // Ajoutez cette ligne pour la relation avec Kpi


    @Enumerated(EnumType.STRING)
    @NotNull(message = "Veuillez spécifier si le problème est interne ou externe")
    private TypeProbleme typeProbleme;

    @NotBlank(message = "Veuillez sélectionner la méthode à utiliser (5P ou Ishikawa)")
    private String methodeUtilisee;

    @NotBlank(message = "Veuillez indiquer l'identification du problème")
    private String identificationProbleme;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date; // Champ pour stocker la date de l'analyse

    // Liste des causes de l'analyse
    @OneToMany(mappedBy = "analyse", cascade = CascadeType.ALL)
    private List<Cause> causes;

    // Constructeurs, getters et setters

    public Analyse() {
    }

    public Analyse(Project project, TypeProbleme typeProbleme, String identificationProbleme, String methodeUtilisee) {
        this.project = project;
        this.typeProbleme = typeProbleme;
        this.identificationProbleme = identificationProbleme;
        this.methodeUtilisee = methodeUtilisee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public TypeProbleme getTypeProbleme() {
        return typeProbleme;
    }

    public void setTypeProbleme(TypeProbleme typeProbleme) {
        this.typeProbleme = typeProbleme;
    }

    public String getIdentificationProbleme() {
        return identificationProbleme;
    }

    public void setIdentificationProbleme(String identificationProbleme) {
        this.identificationProbleme = identificationProbleme;
    }

    public String getMethodeUtilisee() {
        return methodeUtilisee;
    }

    public void setMethodeUtilisee(String methodeUtilisee) {
        this.methodeUtilisee = methodeUtilisee;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Cause> getCauses() {
        return causes;
    }

    public void setCauses(List<Cause> causes) {
        this.causes = causes;
    }
}
