/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matlazar.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.io.StringReader;
import javax.faces.bean.ManagedBean;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.foi.nwtis.matlazar.ws.klijenti.RESTKorisnik;

/**
 *
 * @author HP
 */
@ManagedBean
@Named(value = "registracija")
@SessionScoped
public class Registracija implements Serializable {
private String korisnickoIme = "";
    private String lozinka = "";
    private String poruka = "";
    private String email = "";
    private String poruka_2 = "";

    /**
     * Creates a new instance of Registracija
     */
    public Registracija() {
    }
    
    public String registracija(){
        String korisnik = getKorisnickoIme();
        String loz = getLozinka();
        String email = getEmail();
        String jsonSadrzaj = "";
        String tKorime = "";
        String tPass = "";
        String tEmail = "";
        if(korisnik.isEmpty() || loz.isEmpty() || email.isEmpty()){
            setPoruka_2("");
            setPoruka("Morate unijeti sve podatke/Enter all data");
        }else{
            jsonSadrzaj = RESTKorisnik.preuzmiJednogKorisnika(korisnik);
            JsonReader reader = Json.createReader(new StringReader(jsonSadrzaj));
            JsonObject jo = reader.readObject();
            tKorime = jo.getString("korIme");
            if(tKorime.equals(korisnik)){
                setPoruka_2("");
                setPoruka("Korisničko ime već postoji/Used username");
            }else{
                String rezultat = RESTKorisnik.dodajKorisnikaREST(korisnik, loz, email);
                setPoruka("");
                setPoruka_2("Uspješno dodano/Registration completed");
            }
            
        }
        return "OK";
    }

    public String getKorisnickoIme() {
        return korisnickoIme;
    }

    public void setKorisnickoIme(String korisnickoIme) {
        this.korisnickoIme = korisnickoIme;
    }

    public String getLozinka() {
        return lozinka;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }

    public String getPoruka() {
        return poruka;
    }

    public void setPoruka(String poruka) {
        this.poruka = poruka;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPoruka_2() {
        return poruka_2;
    }

    public void setPoruka_2(String poruka_2) {
        this.poruka_2 = poruka_2;
    }
    
    

    public String prijava() {
        return "Prijava";
    }
    
}
