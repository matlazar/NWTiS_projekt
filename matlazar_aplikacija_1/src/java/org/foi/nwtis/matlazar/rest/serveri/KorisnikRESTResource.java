/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matlazar.rest.serveri;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
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
public class KorisnikRESTResource {

    private String korisnickoIme;

    /**
     * Creates a new instance of KorisnikRESTResource
     */
    private KorisnikRESTResource(String korisnickoIme) {
        this.korisnickoIme = korisnickoIme;
    }

    /**
     * Get instance of the KorisnikRESTResource
     */
    public static KorisnikRESTResource getInstance(String korisnickoIme) {
        // The user may use some kind of persistence mechanism
        // to store and restore instances of KorisnikRESTResource class.
        return new KorisnikRESTResource(korisnickoIme);
    }

    /**
     * Retrieves representation of an instance of
     * org.foi.nwtis.matlazar.rest.serveri.KorisnikRESTResource
     *
     * @param request
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson(@Context HttpServletRequest request) {
        long pocetak = System.currentTimeMillis();
        int status = 51;
        //ArrayList<String> korisnik = new ArrayList<>();
        JsonObjectBuilder job = Json.createObjectBuilder();
        BP_Konfiguracija bpkonf;
        Konfiguracija konf;
        Baza baza;
        String korIme = "";
        String pass = "";
        String id = "";
        String email = "";
        String url = "";
        String IP = "";
        bpkonf = (BP_Konfiguracija) staticContext.getAttribute("BP_Konfig");
        konf = (Konfiguracija) staticContext.getAttribute("Konfig");
        String bpServer = konf.dajPostavku("server.database");
        String bazaIme = konf.dajPostavku("user.database");
        String bpKorisnik = konf.dajPostavku("user.username");
        String bpLozinka = konf.dajPostavku("user.password");
        String driver = bpkonf.getDriverDatabase();
        baza = new Baza(bpServer, bazaIme, bpKorisnik, bpLozinka, driver);
        try {
            url = request.getRequestURI();
            IP = InetAddress.getLocalHost().getHostAddress();
            String upit = "SELECT * FROM korisnik WHERE korisnickoIme = '" + this.korisnickoIme + "'";
            baza.spojiBazu();
            
            ResultSet rs = baza.selectUpit(upit);
            while (rs.next()) {
                korIme = rs.getString("korisnickoIme");
                pass = rs.getString("Lozinka");
                id = Integer.toString(rs.getInt("id"));
                email = rs.getString("email");
                job.add("id", id).add("korIme", korIme).add("lozinka", pass).add("email",email);
            }
            //korisnik.add(korIme);
            //korisnik.add(pass);
            //korisnik.add(id);
            //korisnik.add(email);
            baza.zatvoriBazu();
        } catch (SQLException ex) {
            Logger.getLogger(KorisnikRESTResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(KorisnikRESTResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        int razlika = (int) (System.currentTimeMillis() - pocetak) / 1000;
        String dnevnik = "INSERT INTO dnevnik VALUES (default,'"+korIme+"','"+url+"','"+IP+"', default, "+razlika+", "+status+")";
        baza.spojiBazu();
        baza.insertUpit(dnevnik);
        baza.zatvoriBazu();
        return job.build().toString();
    }

    /**
     * PUT method for updating or creating an instance of KorisnikRESTResource
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }

    /**
     * DELETE method for resource KorisnikRESTResource
     */
    @DELETE
    public void delete() {
    }
}
