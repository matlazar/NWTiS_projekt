/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matlazar.web.dretve;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.foi.nwtis.matlazar.konfiguracije.Konfiguracija;
import org.foi.nwtis.matlazar.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.matlazar.rest.klijenti.OWMKlijent;
import org.foi.nwtis.matlazar.web.podaci.Baza;
import org.foi.nwtis.matlazar.web.podaci.MeteoPodaci;

/**
 *
 * @author HP
 */
public class PreuzimajMeteoPodatke extends Thread {

    private ServletContext sc = null;
    public static boolean pause = true;
    public static boolean stop = true;
    Konfiguracija konf;
    Baza baza;
    BP_Konfiguracija bpkonf;
    SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy hh.mm.ss");

    public void setSc(ServletContext sc) {
        this.sc = sc;
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }

    @Override
    public void run() {
        konf = (Konfiguracija) sc.getAttribute("Konfig");
        bpkonf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");
        String bpServer = konf.dajPostavku("server.database");
        String bazaIme = konf.dajPostavku("user.database");
        String bpKorisnik = konf.dajPostavku("user.username");
        String bpLozinka = konf.dajPostavku("user.password");
        String driver = bpkonf.getDriverDatabase();
        baza = new Baza(bpServer, bazaIme, bpKorisnik, bpLozinka, driver);
        int trajanjePreuzimanja = Integer.parseInt(konf.dajPostavku("mail.timeSecThread"));
        long pocetak = 0;
        long kraj = 0;
        long trajanje = 0;
        String apikey = (String) konf.dajPostavku("apikey");
        while (stop) {
            while (pause) {
                try {
                    pocetak = System.currentTimeMillis();
                    baza.spojiBazu();
                    String upit = "SELECT latitude, longitude FROM uredaji GROUP BY latitude, longitude";
                    ResultSet rs = baza.selectUpit(upit);
                    while (rs.next()) {
                        Float latitude = rs.getFloat("latitude");
                        Float longitude = rs.getFloat("longitude");
                        OWMKlijent ok = new OWMKlijent(apikey);
                        MeteoPodaci mp = ok.getRealTimeWeather(Float.toString(latitude),Float.toString(longitude));
                        String vrijemeOpis = mp.getWeatherValue();
                        int vrijeme = mp.getWeatherNumber();
                        float temp = mp.getTemperatureValue();
                        float tempMin = mp.getTemperatureMin();
                        float tempMax = mp.getTemperatureMax();
                        float vlaga = mp.getHumidityValue();
                        float tlak = mp.getPressureValue();
                        float vjetar = mp.getWindSpeedValue();
                        String vjetarSmjer = mp.getWindDirectionCode();
                        if (vjetarSmjer.equals("")) {
                            vjetarSmjer = "0";
                        }
                        Date datum = new Date();
                        String sql = "SELECT * FROM uredaji WHERE latitude LIKE '" + latitude + "' AND longitude LIKE '" + longitude + "' ";
                        ResultSet rs2 = baza.selectUpit(sql);
                        while(rs2.next()){
                            int id = rs2.getInt("id");
                            String sql2 = "INSERT INTO meteo (idmeteo, id, adresastanice, latitude, longitude, vrijeme, vrijemeopis, temp, tempmin, tempmax, vlaga, tlak, vjetar, vjetarsmjer, preuzeto) "
                            + "VALUES (default," + id + ",'matlazar'," + latitude + "," + longitude + ",'" + vrijeme + "','" + vrijemeOpis + "'," + temp + ","
                            + tempMin + "," + tempMax + "," + vlaga + "," + tlak + "," + vjetar + "," + vjetarSmjer + ",default)";
                            int uspjeh = baza.insertUpit(sql2);
                            if(uspjeh  == 0){
                                System.out.println("Neuspjesno dodavanje u bazu");
                            }else {
                                System.out.println("Uspjesno dodavanje u bazu");
                            }
                        }
                                
                    }
                    baza.zatvoriBazu();
                    kraj = System.currentTimeMillis();
                    trajanje = kraj - pocetak;
                    sleep(trajanjePreuzimanja * 1000 - trajanje);
                } catch (SQLException ex) {
                    Logger.getLogger(PreuzimajMeteoPodatke.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PreuzimajMeteoPodatke.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override
    public synchronized void start() {
        super.start();
    }

}
