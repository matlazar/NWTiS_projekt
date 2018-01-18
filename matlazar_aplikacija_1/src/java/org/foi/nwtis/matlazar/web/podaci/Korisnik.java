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
public class Korisnik {
    private int id;
    private String korIme;
    private String Lozinka;
    private String email;

    public Korisnik() {
    }

    public Korisnik(int id, String korIme, String Lozinka, String email) {
        this.id = id;
        this.korIme = korIme;
        this.Lozinka = Lozinka;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKorIme() {
        return korIme;
    }

    public void setKorIme(String korIme) {
        this.korIme = korIme;
    }

    public String getLozinka() {
        return Lozinka;
    }

    public void setLozinka(String Lozinka) {
        this.Lozinka = Lozinka;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    
}
