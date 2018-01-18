/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matlazar.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import org.foi.nwtis.matlazar.konfiguracije.Konfiguracija;
import org.foi.nwtis.matlazar.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.matlazar.web.podaci.Baza;
import org.foi.nwtis.matlazar.web.podaci.Korisnik;

/**
 *
 * @author HP
 */
@ManagedBean
@Named(value = "pregledKorisnika")
@SessionScoped
public class PregledKorisnika implements Serializable {

    private List<Korisnik> korisnici = new ArrayList<>();
    private String naslov = "Pregled korisnika";
    private int retci;
    ServletContext servletContext;
    Konfiguracija konf;
    BP_Konfiguracija bpkonf;
    Baza baza;

    /**
     * Creates a new instance of PregledKorisnika
     */
    public PregledKorisnika() {
        servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        konf = (Konfiguracija) servletContext.getAttribute("Konfig");
        bpkonf = (BP_Konfiguracija) servletContext.getAttribute("BP_Konfig");
    }

    public void prikaziKorisnike() {
        korisnici.clear();
        try {
            String bpServer = konf.dajPostavku("server.database");
            String bazaIme = konf.dajPostavku("user.database");
            String bpKorisnik = konf.dajPostavku("user.username");
            String bpLozinka = konf.dajPostavku("user.password");
            String driver = bpkonf.getDriverDatabase();
            String upit = "SELECT * FROM korisnik";
            baza = new Baza(bpServer, bazaIme, bpKorisnik, bpLozinka, driver);
            baza.spojiBazu();
            ResultSet rs = baza.selectUpit(upit);
            while (rs.next()) {
                korisnici.add(new Korisnik(rs.getInt("id"),rs.getString("korisnickoIme"),rs.getString("Lozinka"),rs.getString("email")));
            }
            korisnici.size();
            setRetci(Integer.parseInt(konf.dajPostavku("numTableRow")));
            baza.zatvoriBazu();
        } catch (SQLException ex) {
            Logger.getLogger(PregledKorisnika.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Korisnik> getKorisnici() {
        prikaziKorisnike();
        return korisnici;
    }

    public void setKorisnici(List<Korisnik> korisnici) {
        this.korisnici = korisnici;
    }

    public String getNaslov() {
        return naslov;
    }

    public void setNaslov(String naslov) {
        this.naslov = naslov;
    }

    public int getRetci() {
        return retci;
    }

    public void setRetci(int retci) {
        this.retci = retci;
    }
    
    
}
