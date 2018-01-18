/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matlazar.ws.klijenti;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 *
 * @author HP
 */
public class RESTKorisnik {

    public static String dodajKorisnikaREST(String korime, String pass, String email) {
        KorisnikRESTResourceContainerClient korisnikRESTResourceContainerClient = new KorisnikRESTResourceContainerClient();
        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("id", "0");
        job.add("korime", korime);
        job.add("pass", pass);
        job.add("email", email);
        job.add("akt", "dodaj");
        Response rezultat = korisnikRESTResourceContainerClient.postJson(job.build().toString());
        return rezultat.toString();
    }

    public static String azurirajKorisnikaREST(String id,String korime, String pass, String email) {
        KorisnikRESTResourceContainerClient korisnikRESTResourceContainerClient = new KorisnikRESTResourceContainerClient();
        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("id", id);
        job.add("korime", korime);
        job.add("pass", pass);
        job.add("email", email);
        job.add("akt", "azuriraj");
        String json = job.build().toString();
        Response rezultat = korisnikRESTResourceContainerClient.postJson(json);
        return rezultat.getEntity().toString();
    }
    
    public static String preuzmiJednogKorisnika(String korime){
        KorisnikRESTResourceContainerClient korisnikRESTResourceContainerClient = new KorisnikRESTResourceContainerClient(korime);
        String rezultat = korisnikRESTResourceContainerClient.getJson();
        return rezultat;
    }
    
    public static String preuzmiSveKorisnike(){
        KorisnikRESTResourceContainerClient korisnikRESTResourceContainerClient = new KorisnikRESTResourceContainerClient();
        String rezultat = korisnikRESTResourceContainerClient.getJson();
        return rezultat;
    }
    
    static class KorisnikRESTResourceContainerClient {

        private WebTarget webTarget;
        private Client client;
        private static final String BASE_URI = "http://localhost:8084/matlazar_aplikacija_1/webresources";

        public KorisnikRESTResourceContainerClient() {
            client = javax.ws.rs.client.ClientBuilder.newClient();
            webTarget = client.target(BASE_URI).path("korisnikREST");
        }

        public KorisnikRESTResourceContainerClient(String korisnickoIme) {
            client = javax.ws.rs.client.ClientBuilder.newClient();
            webTarget = client.target(BASE_URI).path("korisnikREST/" + korisnickoIme);
        }

        public Response postJson(Object requestEntity) throws ClientErrorException {
            return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), Response.class);
        }

        public String getJson() throws ClientErrorException {
            WebTarget resource = webTarget;
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
        }

        public void close() {
            client.close();
        }
    }

}
