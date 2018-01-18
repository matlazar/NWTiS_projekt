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
import org.foi.nwtis.matlazar.web.podaci.Baza;
import org.foi.nwtis.matlazar.web.podaci.Korisnik;
import static org.foi.nwtis.matlazar.web.slusaci.SlusacAplikacije.staticContext;

/**
 * REST Web Service
 *
 * @author HP
 */
@Path("/korisnikREST")
public class KorisnikRESTResourceContainer {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of KorisnikRESTResourceContainer
     */
    public KorisnikRESTResourceContainer() {
    }

    /**
     * Retrieves representation of an instance of
     * org.foi.nwtis.matlazar.rest.serveri.KorisnikRESTResourceContainer
     *
     * @param request
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson(@Context HttpServletRequest request) {
        long pocetak = System.currentTimeMillis();
        int status = 52;
        ArrayList<Korisnik> korisnik = new ArrayList<>();
        Baza baza;
        Konfiguracija konf = (Konfiguracija) staticContext.getAttribute("Konfig");
        BP_Konfiguracija bpkonf = (BP_Konfiguracija) staticContext.getAttribute("BP_Konfig");
        String bpServer = konf.dajPostavku("server.database");
        String bazaIme = konf.dajPostavku("user.database");
        String bpKorisnik = konf.dajPostavku("user.username");
        String bpLozinka = konf.dajPostavku("user.password");
        String driver = bpkonf.getDriverDatabase();
        baza = new Baza(bpServer, bazaIme, bpKorisnik, bpLozinka, driver);
        String url = "";
        String IP = "";
        int id;
        String korIme = "";
        String pass = "";
        String email = "";

        try {
            url = request.getRequestURI();
            IP = InetAddress.getLocalHost().getHostAddress();
            baza.spojiBazu();
            String upit = "SELECT * FROM korisnik";
            ResultSet rs = baza.selectUpit(upit);
            while (rs.next()) {
                id = rs.getInt("id");
                korIme = rs.getString("korisnickoIme");
                pass = rs.getString("Lozinka");
                email = rs.getString("email");
                korisnik.add(new Korisnik(id, korIme, pass, email));
            }
        } catch (SQLException ex) {
            Logger.getLogger(KorisnikRESTResourceContainer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(KorisnikRESTResourceContainer.class.getName()).log(Level.SEVERE, null, ex);
        }
        JsonArrayBuilder jab = Json.createArrayBuilder();
        for (Korisnik k : korisnik) {
            JsonObjectBuilder job = Json.createObjectBuilder();
            String pEmail = k.getEmail();
            if (k.getEmail() == null) {
                pEmail = "Nije uneseno";
            }
            job.add("id", Integer.toString(k.getId())).add("korIme", k.getKorIme()).add("email", pEmail);
            jab.add(job);
        }
        int razlika = (int) (System.currentTimeMillis() - pocetak) / 1000;
        String dnevnik = "INSERT INTO dnevnik VALUES (default,'nepoznat','" + url + "','" + IP + "', default, " + razlika + ", " + status + ")";
        baza.spojiBazu();
        baza.insertUpit(dnevnik);
        baza.zatvoriBazu();
        return jab.build().toString();
    }

    /**
     * POST method for creating an instance of KorisnikRESTResource
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
        int status = 52;
        
        String idK = jo.getString("id");
        String korime = jo.getString("korime");
        String pass = jo.getString("pass");
        String email = jo.getString("email");
        String akcija = jo.getString("akt");
        String rezultat = "";

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
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(KorisnikRESTResourceContainer.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (akcija.equals("dodaj")) {
            String upit = "SELECT * FROM korisnik WHERE korisnickoIme = '" + korime + "'";
            try {
                baza.spojiBazu();
                ResultSet rs = baza.selectUpit(upit);
                if (rs.next()) {
                    rezultat = "0";
                } else {
                    String insertaj = "INSERT INTO korisnik VALUES(default,'" + korime + "','" + pass + "','" + email + "')";
                    baza.insertUpit(insertaj);
                    rezultat = "1";
                }
                baza.zatvoriBazu();
            } catch (SQLException ex) {
                Logger.getLogger(KorisnikRESTResourceContainer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (akcija.equals("azuriraj")) {
                try {
                    int uspjeh = 0;
                    String upit = "SELECT * FROM korisnik WHERE id = " + Integer.parseInt(idK) + "";
                    baza.spojiBazu();
                    String update = "UPDATE korisnik SET korisnickoIme = '" + korime + "', Lozinka = '" + pass + "', email = '" + email + "' WHERE id = " + Integer.parseInt(idK) + "";
                    ResultSet rs = baza.selectUpit(upit);
                    while (rs.next()) {
                        uspjeh = baza.insertUpit(update);
                    }
                    if (uspjeh == 0) {
                        rezultat = "0";
                    } else {
                        rezultat = "1";
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(KorisnikRESTResourceContainer.class.getName()).log(Level.SEVERE, null, ex);
                }
        } else {
            rezultat = "-1";
        }
        int razlika = (int) (System.currentTimeMillis() - pocetak) / 1000;
        String dnevnik = "INSERT INTO dnevnik VALUES (default,'nepoznat','" + url + "','" + ip + "', default, " + razlika + ", " + status + ")";
        baza.spojiBazu();
        baza.insertUpit(dnevnik);
        baza.zatvoriBazu();
        return Response.created(context.getAbsolutePath()).entity(rezultat).build();
    }

    /**
     * Sub-resource locator method for {korisnickoIme}
     */
    @Path("{korisnickoIme}")
    public KorisnikRESTResource getKorisnikRESTResource(@PathParam("korisnickoIme") String korisnickoIme) {
        return KorisnikRESTResource.getInstance(korisnickoIme);
    }
}
