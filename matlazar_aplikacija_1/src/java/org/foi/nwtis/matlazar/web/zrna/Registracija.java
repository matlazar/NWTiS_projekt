/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matlazar.web.zrna;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
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
@Named(value = "registracija")
@Dependent
public class Registracija {

    private String korisnickoIme;
    private String lozinka;
    private String poruka = "";
    private String email;

    ServletContext servletContext;
    Konfiguracija konf;

    BP_Konfiguracija bpkonf;
    Baza baza;

    /**
     * Creates a new instance of Registracija
     */
    public Registracija() {
        servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        konf = (Konfiguracija) servletContext.getAttribute("Konfig");
        bpkonf = (BP_Konfiguracija) servletContext.getAttribute("BP_Konfig");
    }

    public String registracija() {
        String bpServer = konf.dajPostavku("server.database");
        String bazaIme = konf.dajPostavku("user.database");
        String bpKorisnik = konf.dajPostavku("user.username");
        String bpLozinka = konf.dajPostavku("user.password");
        String driver = bpkonf.getDriverDatabase();
        String naziv = getKorisnickoIme();
        String loz = getLozinka();
        String em = getEmail();
        String upit = "INSERT INTO korisnik VALUES (DEFAULT, '" + naziv + "','" + loz + "','"+ em +"')";
        String upit2 = "SELECT korisnickoIme FROM korisnik WHERE korisnickoIme = '" + naziv + "'";
        ResultSet rs;
        baza = new Baza(bpServer, bazaIme, bpKorisnik, bpLozinka, driver);
        baza.spojiBazu();
        rs = baza.selectUpit(upit2);
        try {
            if (!rs.isBeforeFirst()) {
                baza.insertUpit(upit);
                setPoruka("Uspješno dodano u bazu");
            } else {
                setPoruka("To korisničko ime već postoji");
            }
            baza.zatvoriBazu();
        } catch (SQLException ex) {
            Logger.getLogger(Registracija.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "OK";
    }

    public String prijava() {
        return "Prijava";
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
}
