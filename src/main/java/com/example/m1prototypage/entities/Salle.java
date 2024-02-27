package com.example.m1prototypage.entities;

public class Salle {
    int id;
    String nom;
    Boolean disponible;

    public Salle() {
    }
    public Salle(int id, String nom, Boolean disponible) {
        this.id = id;
        this.nom = nom;
        this.disponible = disponible;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public Boolean getDisponible() {
        return disponible;
    }
    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

}
