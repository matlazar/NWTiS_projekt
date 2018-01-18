/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matlazar.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import org.foi.nwtis.matlazar.konfiguracije.Konfiguracija;
import org.foi.nwtis.matlazar.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.matlazar.web.podaci.Baza;

/**
 *
 * @author HP
 */
@ManagedBean
@Named(value = "prijava")
@SessionScoped
public class Prijava implements Serializable {

    private String korisnickoIme;
    private String lozinka;
    ServletContext servletContext;
    Konfiguracija konf;
    BP_Konfiguracija bpkonf;
    Baza baza;

    /**
     * Creates a new instance of Prijava
     */
    public Prijava() {
        servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        konf = (Konfiguracija) servletContext.getAttribute("Konfig");
        bpkonf = (BP_Konfiguracija) servletContext.getAttribute("BP_Konfig");
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

    public String prijava() {
        String bpServer = konf.dajPostavku("server.database");
        String bazaIme = konf.dajPostavku("user.database");
        String bpKorisnik = konf.dajPostavku("user.username");
        String bpLozinka = konf.dajPostavku("user.password");
        String driver = bpkonf.getDriverDatabase();
        ResultSet rs;
        String korisnik = getKorisnickoIme();
        String loz = getLozinka();
        String upit = "SELECT korisnickoIme, Lozinka FROM korisnik WHERE korisnickoIme = '" + korisnik + "'";
        baza = new Baza(bpServer, bazaIme, bpKorisnik, bpLozinka, driver);
        baza.spojiBazu();
        rs = baza.selectUpit(upit);
        try {
            if (rs.next()) {
                return "OK";

            }
            baza.zatvoriBazu();
        } catch (SQLException ex) {
            Logger.getLogger(Prijava.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "NOTOK";
    }

    public String registracija() {
        return "Registracija";
    }
}
