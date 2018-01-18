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
import java.util.Locale;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.matlazar.podaci.Izbornik;
import static org.foi.nwtis.matlazar.web.slusaci.SlusacAplikacije.staticContext;
import org.foi.nwtis.matlazar.ws.klijenti.RESTKorisnik;

/**
 *
 * @author HP
 */
@ManagedBean
@Named(value = "prijava")
@SessionScoped
public class Prijava implements Serializable {

    private String korisnickoIme = "";
    private String lozinka = "";
    private String poruka = "";
    static final ArrayList<Izbornik> izbornikJezika = new ArrayList<>();
    private String odabraniJezik;
    HttpSession session;
    FacesContext facesContext;

    static {
        izbornikJezika.add(new Izbornik("hrvatski", "hr"));
        izbornikJezika.add(new Izbornik("engleski", "en"));
    }

    /**
     * Creates a new instance of Prijava
     */
    public Prijava() {
        facesContext = FacesContext.getCurrentInstance();
        session = (HttpSession) facesContext.getExternalContext().getSession(true);
    }

    public String prijava() {
        String korisnik = getKorisnickoIme();
        String loz = getLozinka();
        String jsonSadrzaj = "";
        String tKorime = "";
        String tPass = "";
        if (korisnik.isEmpty() || loz.isEmpty()) {
            setPoruka("Morate unijeti i korisničko ime i lozinku");
            return "NOTOK";
        } else {
            jsonSadrzaj = RESTKorisnik.preuzmiJednogKorisnika(korisnik);
            JsonReader reader = Json.createReader(new StringReader(jsonSadrzaj));
            JsonObject jo = reader.readObject();
            tKorime = jo.getString("korIme");
            tPass = jo.getString("lozinka");
            if (korisnik.equals(tKorime) && loz.equals(tPass)) {
                session.setAttribute("korime",tKorime);
                session.setAttribute("pass", tPass);
                setPoruka("");
                return "OK";
            } else {
                setPoruka("Neipravni podaci za prijavu!");
                return "NOTOK";
            }
        }
    }

    public String registracija() {
        return "Registracija";
    }

    public Object odaberiJezik() {
        setOdabraniJezik(odabraniJezik);
        return "PromjenaJezika";
    }

    public String getOdabraniJezik() {
        UIViewRoot view = FacesContext.getCurrentInstance().getViewRoot();
        if (view == null) {
            odabraniJezik = "hr";
        } else {
            Locale lokalniJezik = FacesContext.getCurrentInstance().getViewRoot().getLocale();
            odabraniJezik = lokalniJezik.getLanguage();
        }

        return odabraniJezik;
    }

    public void setOdabraniJezik(String odabraniJezik) {
        this.odabraniJezik = odabraniJezik;
        Locale lokalniJezik = new Locale(odabraniJezik);
        FacesContext.getCurrentInstance().getViewRoot().setLocale(lokalniJezik);
    }

    public ArrayList<Izbornik> getIzbornikJezika() {
        return izbornikJezika;
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

}
