package com.example.hscheduler;

public class Pacient {
    private String nume,prenume,adresa,nr_telefon,afectiune,ocupatie,sex,cnp;
    private int varsta,zi,luna,an,ora,minut;
    private boolean casatorit;

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getCnp() {
        return cnp;
    }

    public void setCnp(String cnp) {
        this.cnp = cnp;
    }

    public int getZi() {
        return zi;
    }

    public void setZi(int zi) {
        this.zi = zi;
    }

    public int getLuna() {
        return luna;
    }

    public void setLuna(int luna) {
        this.luna = luna;
    }

    public int getAn() {
        return an;
    }

    public void setAn(int an) {
        this.an = an;
    }

    public int getOra() {
        return ora;
    }

    public void setOra(int ora) {
        this.ora = ora;
    }

    public int getMinut() {
        return minut;
    }

    public void setMinut(int minut) {
        this.minut = minut;
    }

    public Pacient(String nume, String prenume, String adresa, String nr_telefon, String afectiune, String ocupatie,String sex,int varsta, int zi, int luna, int an, int ora, int minut, boolean casatorit) {
        this.nume = nume;
        this.prenume = prenume;
        this.adresa = adresa;
        this.nr_telefon = nr_telefon;
        this.afectiune = afectiune;
        this.ocupatie = ocupatie;
        this.varsta = varsta;
        this.zi = zi;
        this.luna = luna;
        this.an = an;
        this.ora = ora;
        this.minut = minut;
        this.casatorit = casatorit;
        this.sex=sex;
    }

    public Pacient(){
        this.nume = "";
        this.prenume = "";
        this.adresa = "";
        this.nr_telefon = "";
        this.afectiune = "";
        this.ocupatie = "";
        this.varsta = 0;
        this.casatorit = false;
        this.zi = 0;
        this.luna=0;
        this.an = 0;
        this.ora = 0;
        this.minut = 0;
        this.sex="";
    }

    public Pacient(String nume, String prenume, String adresa, String cnp,String nr_telefon ,String ocupatie,String afectiune ,String sex,int varsta, boolean casatorit) {
        this.nume = nume;
        this.prenume = prenume;
        this.adresa = adresa;
        this.cnp = cnp;
        this.nr_telefon = nr_telefon;
        this.afectiune = afectiune;
        this.ocupatie = ocupatie;
        this.varsta = varsta;
        this.casatorit = casatorit;
        this.sex=sex;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public String getNr_telefon() {
        return nr_telefon;
    }

    public void setNr_telefon(String nr_telefon) {
        this.nr_telefon = nr_telefon;
    }

    public String getAfectiune() {
        return afectiune;
    }

    public void setAfectiune(String afectiune) {
        this.afectiune = afectiune;
    }

    public String getOcupatie() {
        return ocupatie;
    }

    public void setOcupatie(String ocupatie) {
        this.ocupatie = ocupatie;
    }

    public int getVarsta() {
        return varsta;
    }

    public void setVarsta(int varsta) {
        this.varsta = varsta;
    }

    public boolean isCasatorit() {
        return casatorit;
    }

    public void setCasatorit(boolean casatorit) {
        this.casatorit = casatorit;
    }
}
