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
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.matlazar.konfiguracije.Konfiguracija;
import org.foi.nwtis.matlazar.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.matlazar.podaci.Korisnik;
import static org.foi.nwtis.matlazar.web.slusaci.SlusacAplikacije.staticContext;
import org.foi.nwtis.matlazar.ws.klijenti.RESTKorisnik;

/**
 *
 * @author HP
 */
@ManagedBean
@Named(value = "user")
@SessionScoped
public class User implements Serializable {

    private String idUser = "";
    private String korIme = "";
    private String lozinka = "";
    private String email = "";
    private String poruka = "";
    private String poruka_2 = "";
    List<Korisnik> korisnici = new ArrayList<>();
    HttpSession session;
    FacesContext facesContext;
    Konfiguracija konf;
    BP_Konfiguracija bpkonf;
    private int retci;
    private boolean prikazi = false;

    /**
     * Creates a new instance of User
     */
    public User() {
        facesContext = FacesContext.getCurrentInstance();
        session = (HttpSession) facesContext.getExternalContext().getSession(true);
        konf =  (Konfiguracija) staticContext.getAttribute("Konfig");
    }

    public void ulogiraniPodaci() {
        String jsonSadrzaj = RESTKorisnik.preuzmiJednogKorisnika((String) session.getAttribute("korime"));
        JsonReader reader = Json.createReader(new StringReader(jsonSadrzaj));
        JsonObject jo = reader.readObject();
        setIdUser(jo.getString("id"));
        setKorIme(jo.getString("korIme"));
        setLozinka(jo.getString("lozinka"));
        setEmail(jo.getString("email"));
    }

    public void azuriraj() {
        if (korIme.isEmpty() || email.isEmpty() || lozinka.isEmpty()) {
            setPoruka_2("");
            setPoruka("Sva polja moraju biti popunjena/all fileds must have content");
        } else {
            String rezultat = RESTKorisnik.azurirajKorisnikaREST(idUser, korIme, lozinka, email);
            setPoruka("");
            setPoruka_2("Uspješno ste ažurirali podatke");
        }
    }

    public void prikaziKorisnike() {
        korisnici.clear();
        String sviKorisnici = RESTKorisnik.preuzmiSveKorisnike();
        JsonReader reader = Json.createReader(new StringReader(sviKorisnici));
        JsonArray ja = reader.readArray();
        for (int i = 0; i < ja.size(); i++) {
            JsonObject jo = ja.getJsonObject(i);
            korisnici.add(new Korisnik(Integer.parseInt(jo.getString("id")), jo.getString("korIme"), "", jo.getString("email")));
        }
        konf = (Konfiguracija) staticContext.getAttribute("Konfig");
        setRetci(Integer.parseInt(konf.dajPostavku("numTableRow")));
        setPrikazi(true);
    }

    public String getIdUser() {
        ulogiraniPodaci();
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getKorIme() {
        return korIme;
    }

    public void setKorIme(String korIme) {
        this.korIme = korIme;
    }

    public String getLozinka() {
        return lozinka;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Korisnik> getKorisnici() {
        prikaziKorisnike();
        return korisnici;
    }

    public void setKorisnici(List<Korisnik> korisnici) {
        this.korisnici = korisnici;
    }

    public String getPoruka() {
        return poruka;
    }

    public void setPoruka(String poruka) {
        this.poruka = poruka;
    }

    public String getPoruka_2() {
        return poruka_2;
    }

    public void setPoruka_2(String poruka_2) {
        this.poruka_2 = poruka_2;
    }

    public int getRetci() {
        return retci;
    }

    public void setRetci(int retci) {
        this.retci = retci;
    }

    public boolean isPrikazi() {
        return prikazi;
    }

    public void setPrikazi(boolean prikazi) {
        this.prikazi = prikazi;
    }
    
    

}
