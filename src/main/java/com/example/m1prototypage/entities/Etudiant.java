package com.example.m1prototypage.entities;

public class Etudiant extends User{

    private Formation formation;

    public Etudiant() {
    }

    public Etudiant(int id, String username, String password, Formation formation) {
        super(id, username, password);
        this.formation = formation;
    }

}
