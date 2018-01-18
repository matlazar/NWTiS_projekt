/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matlazar.rest.serveri;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.foi.nwtis.matlazar.konfiguracije.Konfiguracija;
import org.foi.nwtis.matlazar.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.matlazar.web.podaci.Baza;
import static org.foi.nwtis.matlazar.web.slusaci.SlusacAplikacije.staticContext;

/**
 * REST Web Service
 *
 * @author HP
 */
public class IoTRESTResource {

    private String id;

    /**
     * Creates a new instance of IoTRESTResource
     */
    private IoTRESTResource(String id) {
        this.id = id;
    }

    /**
     * Get instance of the IoTRESTResource
     */
    public static IoTRESTResource getInstance(String id) {
        // The user may use some kind of persistence mechanism
        // to store and restore instances of IoTRESTResource class.
        return new IoTRESTResource(id);
    }

    /**
     * Retrieves representation of an instance of
     * org.foi.nwtis.matlazar.rest.serveri.IoTRESTResource
     *
     * @param request
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson(@Context HttpServletRequest request) {
        long pocetak = System.currentTimeMillis();
        int status = 61;
        ArrayList<String> IoT = new ArrayList<>();
        BP_Konfiguracija bpkonf;
        Konfiguracija konf;
        Baza baza;
        String idIoT = "";
        String naziv = "";
        String latitude = "";
        String longitude = "";
        String ip = "";
        String url = "";
        bpkonf = (BP_Konfiguracija) staticContext.getAttribute("BP_Konfig");
        konf = (Konfiguracija) staticContext.getAttribute("Konfig");
        String bpServer = konf.dajPostavku("server.database");
        String bazaIme = konf.dajPostavku("user.database");
        String bpKorisnik = konf.dajPostavku("user.username");
        String bpLozinka = konf.dajPostavku("user.password");
        String driver = bpkonf.getDriverDatabase();
        baza = new Baza(bpServer, bazaIme, bpKorisnik, bpLozinka, driver);
        String upit = "SELECT * FROM uredaji WHERE id = " + this.id + "";

        try {
            url = request.getRequestURI();
            ip = InetAddress.getLocalHost().getHostAddress();
            baza.spojiBazu();
            ResultSet rs = baza.selectUpit(upit);
            if (rs.next()) {
                idIoT = Integer.toString(rs.getInt("id"));
                naziv = rs.getString("naziv");
                latitude = Float.toString(rs.getFloat("latitude"));
                longitude = Float.toString(rs.getFloat("longitude"));
            }
            IoT.add(idIoT);
            IoT.add(naziv);
            IoT.add(latitude);
            IoT.add(longitude);
        } catch (UnknownHostException ex) {
            Logger.getLogger(IoTRESTResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(IoTRESTResource.class.getName()).log(Level.SEVERE, null, ex);
        }

        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("id", IoT.get(0)).add("naziv", IoT.get(1)).add("latitude", IoT.get(2)).add("longitude", IoT.get(3));
        int razlika = (int) (System.currentTimeMillis() - pocetak) / 1000;
        String dnevnik = "INSERT INTO dnevnik VALUES (default,'matlazar','" + url + "','" + ip + "', default, " + razlika + ", " + status + ")";
        baza.spojiBazu();
        baza.insertUpit(dnevnik);
        baza.zatvoriBazu();
        return job.build().toString();
    }

    /**
     * PUT method for updating or creating an instance of IoTRESTResource
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }

    /**
     * DELETE method for resource IoTRESTResource
     */
    @DELETE
    public void delete() {
    }
}
