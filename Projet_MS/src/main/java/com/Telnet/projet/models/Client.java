package com.Telnet.projet.models;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Table(name = "Client")
@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private Integer phone;

    private boolean active; // Statut du client (actif/inactif)

    // Autres attributs et méthodes de la classe...
    @OneToMany(mappedBy = "cli", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Project> projects;
    public Client() {
    }

    public Client(String name, Integer phone, boolean active) {
        this.name = name;
        this.phone = phone;
        this.active = active;
    }

    public Integer getPhone() {
        return phone;
    }

    public void setPhone(Integer phone) {
        this.phone = phone;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
