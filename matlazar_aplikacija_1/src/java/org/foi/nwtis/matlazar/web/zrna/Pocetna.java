/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matlazar.web.zrna;

import com.sun.javafx.scene.control.skin.VirtualFlow;
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
import org.foi.nwtis.matlazar.web.podaci.Dnevnik;
import org.foi.nwtis.matlazar.web.podaci.Korisnik;
import org.foi.nwtis.matlazar.web.podaci.MeteoPodaci;
import org.foi.nwtis.matlazar.web.podaci.Uredjaj;

/**
 *
 * @author HP
 */
@ManagedBean
@Named(value = "pocetna")
@SessionScoped
public class Pocetna implements Serializable {

    ServletContext servletContext;
    Konfiguracija konf;
    BP_Konfiguracija bpkonf;
    Baza baza;

    public Pocetna() {
        servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        konf = (Konfiguracija) servletContext.getAttribute("Konfig");
        bpkonf = (BP_Konfiguracija) servletContext.getAttribute("BP_Konfig");
    }

    /**
     * Creates a new instance of Pocetna
     */
    public String dnevnik(){
        return "Dnevnik";
    }
    
    public String korisnik(){
        return "Korisnik";
    }
    
    public String uredaj(){
        return "Uredaj";
    }

}
