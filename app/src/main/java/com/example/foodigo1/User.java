package com.example.foodigo1;

import java.util.List;

public class User {
    private String nom;
    private String prenom;
    private String dateDenaissance;
    private String pseudo;
    private List<String> listOfCapuredFoodie;
    private String passWord;
    public User(String _nom, String _prenom){
        this.nom=nom;
        this.prenom=_prenom;
    }
    public User(String _nom, String _prenom, String _dateDeNaissance, String _pseudo, String _passWord){
        this.nom=nom;
        this.prenom=_prenom;
        this.dateDenaissance=_dateDeNaissance;
        this.pseudo=_pseudo;
        this.passWord=_passWord;
    }

    public List<String> getListOfCapuredFoodie() {
        return listOfCapuredFoodie;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getDateDenaissance() {
        return dateDenaissance;
    }

    public String getPseudo() {
        return pseudo;
    }

    public String getPassWord() {
        return passWord;
    }
}
