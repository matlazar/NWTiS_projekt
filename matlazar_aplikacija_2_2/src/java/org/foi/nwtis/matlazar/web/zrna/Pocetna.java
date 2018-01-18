/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matlazar.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author HP
 */
@ManagedBean
@Named(value = "pocetna")
@SessionScoped
public class Pocetna implements Serializable {

    /**
     * Creates a new instance of Pocetna
     */
    public Pocetna() {
    }
    
    public String korisnik(){
        return "Korisnik";
    }
    
    public String meteo(){
        return "Meteo";
    }
    
    public String serverKomande(){
        return "Server";
    }
    
    public String iot(){
        return "IOT";
    }
    
    public String dnevnik(){
        return "Dnevnik";
    }
    
}
