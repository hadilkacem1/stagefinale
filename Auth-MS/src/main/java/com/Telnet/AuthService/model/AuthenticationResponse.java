package com.Telnet.AuthService.model;



import java.util.List;

public class AuthenticationResponse {
    private String token;
    private String message;
    private String role; // Nouveau champ pour le rôle de l'utilisateur
    private String firstName; // Champ pour le prénom de l'utilisateur
    private String username;
    private String lastName; // Champ pour le nom de famille de l'utilisateur
    private String email; // Champ pour l'email de l'utilisateur
    private List<Activity> activities; // Champ pour stocker les activités de l'utilisateur
    private Integer userId;
    public AuthenticationResponse(String token, String message, String role, String username, String firstName, String lastName, String email,List<Activity> activities,Integer userId) {
        this.token = token;
        this.message = message;
        this.role = role;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.activities=activities;
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }

    public String getRole() {
        return role;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

}
