package com.example.m1prototypage.entities;

public class Formation {
    private int id;
    private String nom;

    public Formation() {
    }
    public Formation(int id, String nom) {
        this.id = id;
        this.nom = nom;
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

}
