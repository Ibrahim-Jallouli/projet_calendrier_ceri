package com.example.m1prototypage.entities;

public class Memo {
    int id;
    String decsription;

    public Memo() {
    }
    public Memo(int id, String decsription) {
        this.id = id;
        this.decsription = decsription;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getDecsription() {
        return decsription;
    }
    public void setDecsription(String decsription) {
        this.decsription = decsription;
    }

}
