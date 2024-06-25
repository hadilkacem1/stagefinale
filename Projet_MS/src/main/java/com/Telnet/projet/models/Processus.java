package com.Telnet.projet.models;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table
@Entity
public class Processus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom ne doit pas être vide")
    private String name;

    @NotBlank(message = "La description ne doit pas être vide")
    private String description;

    @ManyToMany(mappedBy = "processus")
    @JsonIgnore
    private List<Project> projects;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "processus_kpis",
            joinColumns = @JoinColumn(name = "processus_id"),
            inverseJoinColumns = @JoinColumn(name = "kpi_id")
    )
    private Set<Kpi> kpis = new HashSet<>();


    public Set<Kpi> getKpis() {
        return kpis;
    }

    public void setKpis(Set<Kpi> kpis) {
        this.kpis = kpis;
    }

    @JsonIgnore
    @ManyToMany(mappedBy = "processus", cascade = CascadeType.ALL)
    private Set<Activity> activities = new HashSet<>();

    public Processus() {
    }
   public Processus(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Activity> getActivities() {
        return activities;
    }

    public void setActivities(Set<Activity> activities) {
        this.activities = activities;
    }
}
