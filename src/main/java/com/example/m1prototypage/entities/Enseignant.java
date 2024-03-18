package com.example.m1prototypage.entities;

public class Enseignant extends User{

    String mail;

    public Enseignant() {
        super();
    }
    public Enseignant(int id, String username, String password, String mail) {
        super(id, username,  password);
        this.mail = mail;
    }

    public Enseignant(int id, String username, String mail) {
        super(id, username);
        this.mail = mail;
    }

}
