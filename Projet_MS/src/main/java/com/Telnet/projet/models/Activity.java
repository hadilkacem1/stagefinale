package com.Telnet.projet.models;

import com.Telnet.projet.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Table(name = "activities")
@Entity
public class Activity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

    public List<User> getUsers() {
        return users;
    }

    private String name;

    private String description;

    @JsonIgnore
    @ManyToMany(mappedBy = "activities")
    private List<User> users;


  // Assurez-vous que la propriété est correctement nommée et référencée dans la classe Activitysj@
  @JsonIgnore
@OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Project> projects;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "client_id")
    @JsonIgnore
    private Client cli;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "activity_processus",
            joinColumns = @JoinColumn(name = "activity_id"),
            inverseJoinColumns = @JoinColumn(name = "processus_id")
    )
    private Set<Processus> processus;




    public void setUsers(List<User> users) {
        this.users = users;
    }


    public Client getCli() {
        return cli;
    }

    public void setCli(Client cli) {
        this.cli = cli;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
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




    public Set<Processus> getProcessus() {
        return processus;
    }

    public void setProcessus(Set<Processus> processus) {
        this.processus = processus;
    }




}
