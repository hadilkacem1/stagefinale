package com.Telnet.pip.model;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Table(name = "pips")
@Entity
public class Pip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "category_id")
    private Category category;

    @NotBlank
    private String type;

    @NotBlank
    private String interaction;

    public Pip() {
    }

    public Pip(@NotBlank String name, Category category, @NotBlank String type,
               @NotBlank String interaction) {
        this.name = name;
        this.category = category;
        this.type = type;
        this.interaction = interaction;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInteraction() {
        return interaction;
    }

    public void setInteraction(String interaction) {
        this.interaction = interaction;
    }

}
