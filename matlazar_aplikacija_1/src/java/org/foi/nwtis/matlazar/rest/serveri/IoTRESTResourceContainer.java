/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matlazar.rest.serveri;

import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.foi.nwtis.matlazar.konfiguracije.Konfiguracija;
import org.foi.nwtis.matlazar.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.matlazar.rest.klijenti.GMKlijent;
import org.foi.nwtis.matlazar.web.podaci.Baza;
import org.foi.nwtis.matlazar.web.podaci.Lokacija;
import org.foi.nwtis.matlazar.web.podaci.Uredjaj;
import static org.foi.nwtis.matlazar.web.slusaci.SlusacAplikacije.staticContext;

/**
 * REST Web Service
 *
 * @author HP
 */
@Path("/iotREST")
public class IoTRESTResourceContainer {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of IoTRESTResourceContainer
     */
    public IoTRESTResourceContainer() {
    }

    /**
     * Retrieves representation of an instance of
     * org.foi.nwtis.matlazar.rest.serveri.IoTRESTResourceContainer
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson(@Context HttpServletRequest request) {
        long pocetak = System.currentTimeMillis();
        int status = 62;
        ArrayList<Uredjaj> uredjaj = new ArrayList<>();
        Baza baza;
        Konfiguracija konf = (Konfiguracija) staticContext.getAttribute("Konfig");
        BP_Konfiguracija bpkonf = (BP_Konfiguracija) staticContext.getAttribute("BP_Konfig");
        String bpServer = konf.dajPostavku("server.database");
        String bazaIme = konf.dajPostavku("user.database");
        String bpKorisnik = konf.dajPostavku("user.username");
        String bpLozinka = konf.dajPostavku("user.password");
        String driver = bpkonf.getDriverDatabase();
        baza = new Baza(bpServer, bazaIme, bpKorisnik, bpLozinka, driver);
        int id;
        String naziv = "";
        String lat = "";
        String lng = "";
        String url = "";
        String ip = "";
        try {
            url = request.getRequestURI();
            ip = InetAddress.getLocalHost().getHostAddress();
            baza.spojiBazu();
            String upit = "SELECT * FROM uredaji";
            ResultSet rs = baza.selectUpit(upit);
            while (rs.next()) {
                id = rs.getInt("id");
                naziv = rs.getString("naziv");
                lat = Float.toString(rs.getFloat("latitude"));
                lng = Float.toString(rs.getFloat("longitude"));
                uredjaj.add(new Uredjaj(id, naziv, new Lokacija(lat, lng)));
            }
            baza.zatvoriBazu();
        } catch (UnknownHostException ex) {
            Logger.getLogger(IoTRESTResourceContainer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(IoTRESTResourceContainer.class.getName()).log(Level.SEVERE, null, ex);
        }
        JsonArrayBuilder jab = Json.createArrayBuilder();
        for (Uredjaj u : uredjaj) {
            JsonObjectBuilder job = Json.createObjectBuilder();
            job.add("id", Integer.toString(u.getId())).add("naziv", u.getNaziv()).add("latitude", u.getGeoloc().getLatitude()).add("longitude", u.getGeoloc().getLongitude());
            jab.add(job);
        }
        int razlika = (int) (System.currentTimeMillis() - pocetak) / 1000;
        String dnevnik = "INSERT INTO dnevnik VALUES (default,'matlazar','" + url + "','" + ip + "', default, " + razlika + ", " + status + ")";
        baza.spojiBazu();
        baza.insertUpit(dnevnik);
        baza.zatvoriBazu();
        return jab.build().toString();
    }

    /**
     * POST method for creating an instance of IoTRESTResource
     *
     * @param content representation for the new resource
     * @param request
     * @return an HTTP response with content of the created resource
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postJson(String content, @Context HttpServletRequest request) {
        JsonReader reader = Json.createReader(new StringReader(content));
        JsonObject jo = reader.readObject();
        long pocetak = System.currentTimeMillis();
        int status = 63;
        String rezultat = "";
        String idIoT = jo.getString("id");
        String naziv = jo.getString("naziv");
        String adresa = jo.getString("adresa");
        String akcija = jo.getString("akt");

        Baza baza;
        Konfiguracija konf = (Konfiguracija) staticContext.getAttribute("Konfig");
        BP_Konfiguracija bpkonf = (BP_Konfiguracija) staticContext.getAttribute("BP_Konfig");
        String bpServer = konf.dajPostavku("server.database");
        String bazaIme = konf.dajPostavku("user.database");
        String bpKorisnik = konf.dajPostavku("user.username");
        String bpLozinka = konf.dajPostavku("user.password");
        String driver = bpkonf.getDriverDatabase();
        baza = new Baza(bpServer, bazaIme, bpKorisnik, bpLozinka, driver);
        String url = request.getRequestURI();
        String ip = "";
        String maxId = "SELECT MAX(id) AS id FROM uredaji";
        String provjera = "SELECT * FROM uredaji WHERE naziv = '" + naziv + "'";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(IoTRESTResourceContainer.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (akcija.equals("dodaj")) {
            try {
                baza.spojiBazu();
                ResultSet rs = baza.selectUpit(provjera);
                if (rs.next()) {
                    rezultat = "0";
                } else {
                    int id = 0;
                    rs = baza.selectUpit(maxId);
                    if (rs.next()) {
                        id = rs.getInt("id");
                    }
                    id++;
                    GMKlijent gmk = new GMKlijent();
                    Lokacija lok = gmk.getGeoLocation(adresa);
                    String insertiraj = "INSERT INTO uredaji VALUES(" + id + ",'" + naziv + "'," + Float.parseFloat(lok.getLatitude()) + "," + Float.parseFloat(lok.getLongitude()) + ",default,default,default)";
                    baza.insertUpit(insertiraj);
                    rezultat = "1";
                }
                baza.zatvoriBazu();
            } catch (SQLException ex) {
                Logger.getLogger(IoTRESTResourceContainer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (akcija.equals("azuriraj")) {
                try {
                    GMKlijent gmk = new GMKlijent();
                    Lokacija lok2 = gmk.getGeoLocation(adresa);
                    int uspjeh = 0;
                    baza.spojiBazu();
                    String provjera2 = "SELECT * FROM uredaji WHERE id = " + Integer.parseInt(idIoT) + "";
                    String update = "UPDATE uredaji SET naziv = '" + naziv + "',latitude = " + Float.parseFloat(lok2.getLatitude()) + ", longitude = " + Float.parseFloat(lok2.getLongitude()) + ", vrijeme_promjene = NOW() WHERE id =" + Integer.parseInt(idIoT) + "";
                    ResultSet rs = baza.selectUpit(provjera2);
                    while (rs.next()) {
                        uspjeh = baza.insertUpit(update);
                    }
                    if (uspjeh == 0) {
                        rezultat = "0";
                    } else {
                        rezultat = "1";
                    }
                    baza.zatvoriBazu();
                } catch (SQLException ex) {
                    Logger.getLogger(IoTRESTResourceContainer.class.getName()).log(Level.SEVERE, null, ex);
                }
        } else {
            rezultat = "-1";
        }
        int razlika = (int) (System.currentTimeMillis() - pocetak) / 1000;
        String dnevnik = "INSERT INTO dnevnik VALUES (default,'matlazar','" + url + "','" + ip + "', default, " + razlika + ", " + status + ")";
        baza.spojiBazu();
        baza.insertUpit(dnevnik);
        baza.zatvoriBazu();
        return Response.created(context.getAbsolutePath()).entity(rezultat).build();
    }

    /**
     * Sub-resource locator method for {id}
     */
    @Path("{id}")
    public IoTRESTResource getIoTRESTResource(@PathParam("id") String id) {
        return IoTRESTResource.getInstance(id);
    }
}
