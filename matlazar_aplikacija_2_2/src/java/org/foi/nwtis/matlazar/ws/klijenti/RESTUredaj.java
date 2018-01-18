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
public class RESTUredaj {

    public static String dodajIoTREST(String naziv, String adresa) {
        IoTRESTResourceContainerClient ioTRESTResourceContainerClient = new IoTRESTResourceContainerClient();
        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("id", "0");
        job.add("naziv", naziv);
        job.add("adresa", adresa);
        job.add("akt", "dodaj");
        Response rezultat = ioTRESTResourceContainerClient.postJson(job.build().toString());
        return rezultat.toString();
    }

    public static String azurirajIoTREST(String id, String naziv, String adresa) {
        IoTRESTResourceContainerClient ioTRESTResourceContainerClient = new IoTRESTResourceContainerClient();
        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("id", id);
        job.add("naziv", naziv);
        job.add("adresa", adresa);
        job.add("akt", "azuriraj");
        Response rezultat = ioTRESTResourceContainerClient.postJson(job.build().toString());
        return rezultat.toString();
    }

    public static String preuzmiJedanIoT(int id) {
        IoTRESTResourceContainerClient ioTRESTResourceContainerClient = new IoTRESTResourceContainerClient(id);
        String rezultat = ioTRESTResourceContainerClient.getJson();
        return rezultat;
    }

    public static String preuzmiSveIoT() {
        IoTRESTResourceContainerClient ioTRESTResourceContainerClient = new IoTRESTResourceContainerClient();
        String rezultat = ioTRESTResourceContainerClient.getJson();
        return rezultat;
    }

    static class IoTRESTResourceContainerClient {

        private WebTarget webTarget;
        private Client client;
        private static final String BASE_URI = "http://localhost:8084/matlazar_aplikacija_1/webresources";

        public IoTRESTResourceContainerClient() {
            client = javax.ws.rs.client.ClientBuilder.newClient();
            webTarget = client.target(BASE_URI).path("iotREST");
        }

        public IoTRESTResourceContainerClient(int id) {
            client = javax.ws.rs.client.ClientBuilder.newClient();
            webTarget = client.target(BASE_URI).path("iotREST/" + id);
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
