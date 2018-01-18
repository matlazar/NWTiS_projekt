/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.foi.nwtis.matlazar.podaci;


/**
 *
 * @author Matija Lazar
 * @author 
 */
public class Uredjaj {
    private int uid;
    private String naziv;
    private Lokacija geoloc;

    public Uredjaj() {
    }

    public Uredjaj(int uid, String naziv, Lokacija geoloc) {
        this.uid = uid;
        this.naziv = naziv;
        this.geoloc = geoloc;
    }

    public Lokacija getGeoloc() {
        return geoloc;
    }

    public void setGeoloc(Lokacija geoloc) {
        this.geoloc = geoloc;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }        
}
