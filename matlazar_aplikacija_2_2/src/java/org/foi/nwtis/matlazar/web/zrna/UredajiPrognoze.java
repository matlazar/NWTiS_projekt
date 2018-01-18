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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.matlazar.podaci.Izbornik;
import org.foi.nwtis.matlazar.ws.klijenti.MeteoPodaci;
import org.foi.nwtis.matlazar.ws.klijenti.MeteoWsKlijent;
import org.foi.nwtis.matlazar.ws.klijenti.RESTUredaj;
import org.foi.nwtis.matlazar.ws.klijenti.UnknownHostException_Exception;

/**
 *
 * @author HP
 */
@ManagedBean
@Named(value = "uredajiPrognoze")
@SessionScoped
public class UredajiPrognoze implements Serializable {
    
    private String azurirajId;
    private String azurirajNaziv;
    private String azurirajAdresa;
    private List<Izbornik> raspoloziviIoT = new ArrayList<>();
    private List<MeteoPodaci> mpo = new ArrayList<>();
    String jedanRaspolozivihIoT;
    private boolean meteoPodaci = false;
    private String tekst = "";
    private String poruka = "";
    private String gumbVazeci = "Pregledaj prognozu";
    private String gumbZadnjih = "Pregled zadnjih meteo podataka";
    HttpSession session;
    FacesContext facesContext;

    /**
     * Creates a new instance of UredajiPrognoze
     */
    public UredajiPrognoze() {
        facesContext = FacesContext.getCurrentInstance();
        session = (HttpSession) facesContext.getExternalContext().getSession(true);
    }
    
    private void preuzmiIoT() {
        raspoloziviIoT.clear();
        String sviUredaji = RESTUredaj.preuzmiSveIoT();
        JsonReader reader = Json.createReader(new StringReader(sviUredaji));
        JsonArray ja = reader.readArray();
        for (int i = 0; i < ja.size(); i++) {
            JsonObject jo = ja.getJsonObject(i);
            jo.getString("naziv");
            raspoloziviIoT.add(new Izbornik(jo.getString("naziv"), jo.getString("id")));
        }
    }
    
    public void dajUredaj() {
        try {
            String naziv = "";
            String id = getJedanRaspolozivihIoT();
            String korime = (String) session.getAttribute("korime");
            String passw = (String) session.getAttribute("pass");
            String dajAdresu = MeteoWsKlijent.dajAdresuIoTUredaja(Integer.parseInt(id), korime, passw);
            for (Izbornik i : raspoloziviIoT) {
                if (i.getVrijednost().equals(id)) {
                    naziv = i.getLabela();
                    break;
                }
            }
            setAzurirajId(id);
            setAzurirajNaziv(naziv);
            setAzurirajAdresa(dajAdresu);
        } catch (UnknownHostException_Exception ex) {
            Logger.getLogger(UredajiPrognoze.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void azurirajIoT() {
        String naziv = getAzurirajNaziv();
        String id = getAzurirajId();
        String adresa = getAzurirajAdresa();
        if (naziv.isEmpty() || adresa.isEmpty()) {
            setPoruka("Sva polja moraju biti popunjena");
        } else {
            RESTUredaj.azurirajIoTREST(id, naziv, adresa);
            setPoruka("Uspjesno");
        }
    }
    
    public void dajMeteoPodatkeVazece() {
        try {
            String id = getJedanRaspolozivihIoT();
            String korime = (String) session.getAttribute("korime");
            String passw = (String) session.getAttribute("pass");
            mpo = MeteoWsKlijent.vazeciPodaciZaOdabraniIoT(Integer.parseInt(id), korime, passw);
            setMeteoPodaci(true);
        } catch (UnknownHostException_Exception ex) {
            Logger.getLogger(UredajiPrognoze.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void dajMeteoPodatkeZadnje() {
        try {
            String id = getJedanRaspolozivihIoT();
            String korime = (String) session.getAttribute("korime");
            String passw = (String) session.getAttribute("pass");
            mpo = MeteoWsKlijent.posljednihNPrognozaZaUredaj(Integer.parseInt(id), "1", korime, passw);
            setMeteoPodaci(true);
        } catch (UnknownHostException_Exception ex) {
            Logger.getLogger(UredajiPrognoze.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void zatvori() {
        setMeteoPodaci(false);
    }
    
    public String getAzurirajId() {
        return azurirajId;
    }
    
    public void setAzurirajId(String azurirajId) {
        this.azurirajId = azurirajId;
    }
    
    public String getAzurirajNaziv() {
        return azurirajNaziv;
    }
    
    public void setAzurirajNaziv(String azurirajNaziv) {
        this.azurirajNaziv = azurirajNaziv;
    }
    
    public String getAzurirajAdresa() {
        return azurirajAdresa;
    }
    
    public void setAzurirajAdresa(String azurirajAdresa) {
        this.azurirajAdresa = azurirajAdresa;
    }
    
    public List<Izbornik> getRaspoloziviIoT() {
        return raspoloziviIoT;
    }
    
    public void setRaspoloziviIoT(List<Izbornik> raspoloziviIoT) {
        this.raspoloziviIoT = raspoloziviIoT;
    }
    
    public boolean isMeteoPodaci() {
        return meteoPodaci;
    }
    
    public void setMeteoPodaci(boolean meteoPodaci) {
        this.meteoPodaci = meteoPodaci;
    }
    
    public String getTekst() {
        preuzmiIoT();
        return tekst;
    }
    
    public void setTekst(String tekst) {
        this.tekst = tekst;
    }
    
    public String getJedanRaspolozivihIoT() {
        return jedanRaspolozivihIoT;
    }
    
    public void setJedanRaspolozivihIoT(String jedanRaspolozivihIoT) {
        this.jedanRaspolozivihIoT = jedanRaspolozivihIoT;
    }
    
    public String getPoruka() {
        return poruka;
    }
    
    public void setPoruka(String poruka) {
        this.poruka = poruka;
    }
    
    public List<MeteoPodaci> getMpo() {
        return mpo;
    }
    
    public void setMpo(List<MeteoPodaci> mpo) {
        this.mpo = mpo;
    }
    
    public String getGumbVazeci() {
        return gumbVazeci;
    }
    
    public void setGumbVazeci(String gumbVazeci) {
        this.gumbVazeci = gumbVazeci;
    }
    
    public String getGumbZadnjih() {
        return gumbZadnjih;
    }
    
    public void setGumbZadnjih(String gumbZadnjih) {
        this.gumbZadnjih = gumbZadnjih;
    }
    
}
