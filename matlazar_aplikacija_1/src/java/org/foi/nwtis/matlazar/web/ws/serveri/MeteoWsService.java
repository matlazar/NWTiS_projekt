/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matlazar.web.ws.serveri;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.faces.context.FacesContext;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import org.foi.nwtis.matlazar.konfiguracije.Konfiguracija;
import org.foi.nwtis.matlazar.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.matlazar.rest.klijenti.GMKlijent;
import org.foi.nwtis.matlazar.rest.klijenti.OWMKlijent;
import org.foi.nwtis.matlazar.web.dretve.ServerSocketThread;
import org.foi.nwtis.matlazar.web.podaci.Baza;
import org.foi.nwtis.matlazar.web.podaci.MeteoPodaci;

/**
 *
 * @author HP
 */
@WebService(serviceName = "MeteoWsService")
public class MeteoWsService {

    @Resource
    private WebServiceContext context;

    /**
     * Web service operation
     */
    @WebMethod(operationName = "dajZadnjeMeteoPodatkeZaUredaj")
    public List<MeteoPodaci> dajZadnjeMeteoPodatkeZaUredaj(@WebParam(name = "id") int id, @WebParam(name = "korime") String korime, @WebParam(name = "pass") String pass) throws UnknownHostException {
        long pocetak = System.currentTimeMillis();
        ServletContext sc = (ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
        HttpServletRequest request = (HttpServletRequest) context.getMessageContext().get(MessageContext.SERVLET_REQUEST);
        String IP = InetAddress.getLocalHost().getHostAddress();
        String url = request.getRequestURI();
        BP_Konfiguracija bpkonf;
        Konfiguracija konf;
        Baza baza;
        List<MeteoPodaci> mp = new ArrayList<>();
        bpkonf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");
        konf = (Konfiguracija) sc.getAttribute("Konfig");
        String bpServer = konf.dajPostavku("server.database");
        String bazaIme = konf.dajPostavku("user.database");
        String bpKorisnik = konf.dajPostavku("user.username");
        String bpLozinka = konf.dajPostavku("user.password");
        String driver = bpkonf.getDriverDatabase();
        baza = new Baza(bpServer, bazaIme, bpKorisnik, bpLozinka, driver);
        try {
            String upit = "SELECT * FROM meteo WHERE id=" + id + " ORDER BY idmeteo DESC";
            baza.spojiBazu();
            if (!autentikacija(korime, pass, baza)) {
                baza.zatvoriBazu();
                return null;
            }
            ResultSet rs = baza.selectUpit(upit);
            if (rs.next()) {
                mp.add(new MeteoPodaci(new Date(), new Date(), rs.getFloat("temp"), rs.getFloat("tempmin"), rs.getFloat("tempmax"), "C", rs.getFloat("vlaga"), "%", rs.getFloat("tlak"), "hPa", rs.getFloat("vjetar"), "", rs.getFloat("vjetarsmjer"), "", "", 0, "", "", 0.0f, "", "", Integer.parseInt(rs.getString("vrijeme")), rs.getString("vrijemeopis"), "", (Date) rs.getTimestamp("preuzeto")));
            }
            baza.zatvoriBazu();

        } catch (SQLException ex) {
            Logger.getLogger(MeteoWsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        int razlika = (int) (System.currentTimeMillis() - pocetak) / 1000;
        dodajUDnevnik(korime, url, IP, razlika, 41, baza);
        return mp;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "posljednihNPrognozaZaUredaj")
    public List<MeteoPodaci> posljednihNPrognozaZaUredaj(@WebParam(name = "id") int id, @WebParam(name = "brPrognoza") String brPrognoza, @WebParam(name = "korime") String korime, @WebParam(name = "pass") String pass) throws UnknownHostException {
        long pocetak = System.currentTimeMillis();
        ServletContext sc = (ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
        HttpServletRequest request = (HttpServletRequest) context.getMessageContext().get(MessageContext.SERVLET_REQUEST);
        String IP = InetAddress.getLocalHost().getHostAddress();
        String url = request.getRequestURI();
        BP_Konfiguracija bpkonf;
        Konfiguracija konf;
        Baza baza;
        List<MeteoPodaci> mp = new ArrayList<>();
        bpkonf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");
        konf = (Konfiguracija) sc.getAttribute("Konfig");
        String bpServer = konf.dajPostavku("server.database");
        String bazaIme = konf.dajPostavku("user.database");
        String bpKorisnik = konf.dajPostavku("user.username");
        String bpLozinka = konf.dajPostavku("user.password");
        String driver = bpkonf.getDriverDatabase();
        baza = new Baza(bpServer, bazaIme, bpKorisnik, bpLozinka, driver);
        try {
            String upit = "SELECT * FROM meteo WHERE id=" + id + " ORDER BY idmeteo DESC LIMIT " + brPrognoza + "";
            baza.spojiBazu();
            if (!autentikacija(korime, pass, baza)) {
                baza.zatvoriBazu();
                return null;
            }
            ResultSet rs = baza.selectUpit(upit);
            while (rs.next()) {
                mp.add(new MeteoPodaci(new Date(), new Date(), rs.getFloat("temp"), rs.getFloat("tempmin"), rs.getFloat("tempmax"), "C", rs.getFloat("vlaga"), "%", rs.getFloat("tlak"), "hPa", rs.getFloat("vjetar"), "", rs.getFloat("vjetarsmjer"), "", "", 0, "", "", 0.0f, "", "", Integer.parseInt(rs.getString("vrijeme")), rs.getString("vrijemeopis"), "", (Date) rs.getTimestamp("preuzeto")));
            }
            baza.zatvoriBazu();
        } catch (SQLException ex) {
            Logger.getLogger(MeteoWsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        int razlika = (int) (System.currentTimeMillis() - pocetak) / 1000;
        dodajUDnevnik(korime, url, IP, razlika, 42, baza);
        return mp;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "vazeciPodaciZaOdabraniIoT")
    public List<MeteoPodaci> vazeciPodaciZaOdabraniIoT(@WebParam(name = "id") int id, @WebParam(name = "korime") String korime, @WebParam(name = "pass") String pass) throws UnknownHostException {
        long pocetak = System.currentTimeMillis();
        ServletContext sc = (ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
        HttpServletRequest request = (HttpServletRequest) context.getMessageContext().get(MessageContext.SERVLET_REQUEST);
        String IP = InetAddress.getLocalHost().getHostAddress();
        String url = request.getRequestURI();
        BP_Konfiguracija bpkonf;
        Konfiguracija konf;
        Baza baza;
        List<MeteoPodaci> mpo = new ArrayList<>();
        SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy hh.mm.ss.zzz");
        bpkonf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");
        konf = (Konfiguracija) sc.getAttribute("Konfig");
        String bpServer = konf.dajPostavku("server.database");
        String bazaIme = konf.dajPostavku("user.database");
        String bpKorisnik = konf.dajPostavku("user.username");
        String bpLozinka = konf.dajPostavku("user.password");
        String apikey = (String) konf.dajPostavku("apikey");
        String driver = bpkonf.getDriverDatabase();
        baza = new Baza(bpServer, bazaIme, bpKorisnik, bpLozinka, driver);
        try {
            String upit = "SELECT latitude,longitude FROM uredaji WHERE id = " + id + "";
            baza.spojiBazu();
            if (!autentikacija(korime, pass, baza)) {
                baza.zatvoriBazu();
                return null;
            }
            ResultSet rs = baza.selectUpit(upit);
            if (rs.next()) {
                String latitude = Float.toString(rs.getFloat("latitude"));
                String longitude = Float.toString(rs.getFloat("longitude"));
                OWMKlijent ok = new OWMKlijent(apikey);
                MeteoPodaci mp = ok.getRealTimeWeather(latitude, longitude);
                String vrijemeOpis = mp.getWeatherValue();
                String vrijeme = Integer.toString(mp.getWeatherNumber());
                String temp = mp.getTemperatureValue().toString();
                String tempMin = mp.getTemperatureMin().toString();
                String tempMax = mp.getTemperatureMax().toString();
                String vlaga = mp.getHumidityValue().toString();
                String tlak = mp.getPressureValue().toString();
                String vjetar = mp.getWindSpeedValue().toString();
                String vjetarSmjer = mp.getWindDirectionCode();
                if (vjetarSmjer.equals("")) {
                    vjetarSmjer = "0";
                }

                mpo.add(new MeteoPodaci(new Date(), new Date(), Float.parseFloat(temp), Float.parseFloat(tempMin), Float.parseFloat(tempMax), "C", Float.parseFloat(vlaga), "%", Float.parseFloat(tlak), "hPa", Float.parseFloat(vjetar), "", Float.parseFloat(vjetarSmjer), "", "", 0, "", "", 0.0f, "", "", Integer.parseInt(vrijeme), vrijemeOpis, "", new Date()));
            }
            baza.zatvoriBazu();
        } catch (SQLException ex) {
            Logger.getLogger(MeteoWsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        int razlika = (int) (System.currentTimeMillis() - pocetak) / 1000;
        dodajUDnevnik(korime, url, IP, razlika, 43, baza);
        return mpo;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "dajMeteoPodatkeUVremenskomIntervalu")
    public List<MeteoPodaci> dajMeteoPodatkeUVremenskomIntervalu(@WebParam(name = "id") int id, @WebParam(name = "korime") String korime, @WebParam(name = "pass") String pass, @WebParam(name = "datum_od") String datum_od, @WebParam(name = "datum_do") String datum_do) throws UnknownHostException {
        long pocetak = System.currentTimeMillis();
        ServletContext sc = (ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
        HttpServletRequest request = (HttpServletRequest) context.getMessageContext().get(MessageContext.SERVLET_REQUEST);
        String IP = InetAddress.getLocalHost().getHostAddress();
        String url = request.getRequestURI();
        BP_Konfiguracija bpkonf;
        Konfiguracija konf;
        Baza baza;
        List<MeteoPodaci> mpo = new ArrayList<>();
        bpkonf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");
        konf = (Konfiguracija) sc.getAttribute("Konfig");
        String bpServer = konf.dajPostavku("server.database");
        String bazaIme = konf.dajPostavku("user.database");
        String bpKorisnik = konf.dajPostavku("user.username");
        String bpLozinka = konf.dajPostavku("user.password");
        String driver = bpkonf.getDriverDatabase();
        baza = new Baza(bpServer, bazaIme, bpKorisnik, bpLozinka, driver);
        try {
            String upit = "SELECT * FROM meteo WHERE DATE(preuzeto) >='" + datum_od + "' AND DATE(preuzeto)<= '" + datum_do + "' AND id = '" + id + "'";
            baza.spojiBazu();
            if (!autentikacija(korime, pass, baza)) {
                baza.zatvoriBazu();
                return null;
            }
            ResultSet rs = baza.selectUpit(upit);
            while (rs.next()) {
                mpo.add(new MeteoPodaci(new Date(), new Date(), rs.getFloat("temp"), rs.getFloat("tempmin"), rs.getFloat("tempmax"), "C", rs.getFloat("vlaga"), "%", rs.getFloat("tlak"), "hPa", rs.getFloat("vjetar"), "", rs.getFloat("vjetarsmjer"), "", "", 0, "", "", 0.0f, "", "", Integer.parseInt(rs.getString("vrijeme")), rs.getString("vrijemeopis"), "", (Date) rs.getTimestamp("preuzeto")));
            }
            baza.zatvoriBazu();
        } catch (SQLException ex) {
            Logger.getLogger(MeteoWsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        int razlika = (int) (System.currentTimeMillis() - pocetak) / 1000;
        dodajUDnevnik(korime, url, IP, razlika, 44, baza);
        return mpo;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "dajAdresuIoTUredaja")
    public String dajAdresuIoTUređaja(@WebParam(name = "id") int id, @WebParam(name = "korime") String korime, @WebParam(name = "pass") String pass) throws UnknownHostException {
        long pocetak = System.currentTimeMillis();
        ServletContext sc = (ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
        HttpServletRequest request = (HttpServletRequest) context.getMessageContext().get(MessageContext.SERVLET_REQUEST);
        String IP = InetAddress.getLocalHost().getHostAddress();
        String url = request.getRequestURI();
        BP_Konfiguracija bpkonf;
        Konfiguracija konf;
        Baza baza;
        bpkonf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");
        konf = (Konfiguracija) sc.getAttribute("Konfig");
        String bpServer = konf.dajPostavku("server.database");
        String bazaIme = konf.dajPostavku("user.database");
        String bpKorisnik = konf.dajPostavku("user.username");
        String bpLozinka = konf.dajPostavku("user.password");
        String driver = bpkonf.getDriverDatabase();
        String adresa = null;
        String latitude = "";
        String longitude = "";
        baza = new Baza(bpServer, bazaIme, bpKorisnik, bpLozinka, driver);
        try {
            String upit = "SELECT latitude, longitude FROM uredaji WHERE id = '" + id + "'";
            baza.spojiBazu();
            if (!autentikacija(korime, pass, baza)) {
                baza.zatvoriBazu();
                return null;
            }
            ResultSet rs = baza.selectUpit(upit);
            while (rs.next()) {
                latitude = Float.toString(rs.getFloat("latitude"));
                longitude = Float.toString(rs.getFloat("longitude"));
            }
            String latlng = latitude + "," + longitude;
            GMKlijent gmk = new GMKlijent();
            adresa = gmk.getAdresa(latlng);
            baza.zatvoriBazu();
        } catch (SQLException ex) {
            Logger.getLogger(MeteoWsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        int razlika = (int) (System.currentTimeMillis() - pocetak) / 1000;
        dodajUDnevnik(korime, url, IP, razlika, 45, baza);
        return adresa;
    }

    public boolean autentikacija(String korIme, String pass, Baza baza) {
        boolean provjera = false;
        try {
            String upit = "SELECT * FROM korisnik WHERE korisnickoIme = '" + korIme + "' AND Lozinka = '" + pass + "'";
            ResultSet rs = baza.selectUpit(upit);
            if (rs.next()) {
                provjera = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MeteoWsService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return provjera;
    }

    public int dodajUDnevnik(String korisnik, String url, String ipadresa, int trajanje, int status, Baza baza) {
        int uspjesno = 0;
        String upit = "INSERT INTO dnevnik VALUES(default,'" + korisnik + "','" + url + "','" + ipadresa + "',default," + trajanje + "," + status + ")";
        baza.spojiBazu();
        uspjesno = baza.insertUpit(upit);
        baza.zatvoriBazu();
        return uspjesno;
    }

}
