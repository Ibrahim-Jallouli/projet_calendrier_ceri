package com.example.m1prototypage.entities;

import java.util.Date;

public class Seance {
    private String uid;
    private Date dtStart;
    private Date dtEnd;
    private Matiere matiere;
    private Enseignant enseignant;
    private Salle salle;
    private Formation formation;
    private TYPE type;
    private Memo memo;

    public Seance() {
    }

    public Seance(String uid, Date dtStart, Date dtEnd, Matiere matiere, Enseignant enseignant, Salle salle, Formation formation, TYPE type, Memo memo) {
        this.uid = uid;
        this.dtStart = dtStart;
        this.dtEnd = dtEnd;
        this.matiere = matiere;
        this.enseignant = enseignant;
        this.salle = salle;
        this.formation = formation;
        this.type = type;
        this.memo = memo;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Date getDtStart() {
        return dtStart;
    }

    public void setDtStart(Date dtStart) {
        this.dtStart = dtStart;
    }

    public Date getDtEnd() {
        return dtEnd;
    }

    public void setDtEnd(Date dtEnd) {
        this.dtEnd = dtEnd;
    }

    public Matiere getMatiere() {
        return matiere;
    }

    public void setMatiere(Matiere matiere) {
        this.matiere = matiere;
    }

    public Enseignant getEnseignant() {
        return enseignant;
    }

    public void setEnseignant(Enseignant enseignants) {
        this.enseignant = enseignants;
    }

    public Salle getSalle() {
        return salle;
    }

    public void setSalle(Salle salle) {
        this.salle = salle;
    }

    public Formation getFormation() {
        return formation;
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public Memo getMemo() {
        return memo;
    }

    public void setMemo(Memo memo) {
        this.memo = memo;
    }

    @Override
    public String toString() {
        return "Seance{" +
                "dtStart=" + dtStart +
                ", dtEnd=" + dtEnd +
                ", salle = "+ salle+


                '}';
    }
}
