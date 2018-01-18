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
import org.foi.nwtis.matlazar.web.podaci.Lokacija;
import org.foi.nwtis.matlazar.web.podaci.Uredjaj;

/**
 *
 * @author HP
 */
@ManagedBean
@Named(value = "pregledUredaja")
@SessionScoped
public class PregledUredaja implements Serializable {

    private List<Uredjaj> uredaji = new ArrayList<>();
    private String naslov = "Pregled IoT Uredaja";
    private int retci;
    ServletContext servletContext;
    Konfiguracija konf;
    BP_Konfiguracija bpkonf;
    Baza baza;

    /**
     * Creates a new instance of PregledUredaja
     */
    public PregledUredaja() {
        servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        konf = (Konfiguracija) servletContext.getAttribute("Konfig");
        bpkonf = (BP_Konfiguracija) servletContext.getAttribute("BP_Konfig");
    }
    
    public void prikaziUredaje(){
        uredaji.clear();
        try {
            String bpServer = konf.dajPostavku("server.database");
            String bazaIme = konf.dajPostavku("user.database");
            String bpKorisnik = konf.dajPostavku("user.username");
            String bpLozinka = konf.dajPostavku("user.password");
            String driver = bpkonf.getDriverDatabase();
            String upit = "SELECT * FROM uredaji";
            baza = new Baza(bpServer, bazaIme, bpKorisnik, bpLozinka, driver);
            baza.spojiBazu();
            ResultSet rs = baza.selectUpit(upit);
            while (rs.next()) {
                uredaji.add(new Uredjaj(rs.getInt("id"),rs.getString("naziv"),new Lokacija(Float.toString(rs.getFloat("latitude")), Float.toString(rs.getFloat("longitude")))));
            }
            uredaji.size();
            setRetci(Integer.parseInt(konf.dajPostavku("numTableRow")));
            baza.zatvoriBazu();
        } catch (SQLException ex) {
            Logger.getLogger(PregledUredaja.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public List<Uredjaj> getUredaji() {
        prikaziUredaje();
        return uredaji;
    }

    public void setUredaji(List<Uredjaj> uredaji) {
        this.uredaji = uredaji;
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
