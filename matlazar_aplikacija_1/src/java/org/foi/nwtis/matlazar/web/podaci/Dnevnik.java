/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matlazar.web.podaci;

/**
 *
 * @author HP
 */
public class Dnevnik {
    
    private int id;
    private String korisnik;
    private String url;
    private String ipadresa;
    private String vrijeme;
    private int trajanje;
    private int status;

    public Dnevnik() {
    }

    public Dnevnik(int id, String korisnik, String url, String ipadresa, String vrijeme, int trajanje, int status) {
        this.id = id;
        this.korisnik = korisnik;
        this.url = url;
        this.ipadresa = ipadresa;
        this.vrijeme = vrijeme;
        this.trajanje = trajanje;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(String korisnik) {
        this.korisnik = korisnik;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIpadresa() {
        return ipadresa;
    }

    public void setIpadresa(String ipadresa) {
        this.ipadresa = ipadresa;
    }

    public String getVrijeme() {
        return vrijeme;
    }

    public void setVrijeme(String vrijeme) {
        this.vrijeme = vrijeme;
    }

    public int getTrajanje() {
        return trajanje;
    }

    public void setTrajanje(int trajanje) {
        this.trajanje = trajanje;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
    
    
}
