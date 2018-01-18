/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matlazar.ws.klijenti;


/**
 *
 * @author HP
 */
public class MeteoWsKlijent {

    public static String dajAdresuIoTUredaja(int id, java.lang.String korime, java.lang.String pass) throws UnknownHostException_Exception {
        org.foi.nwtis.matlazar.ws.klijenti.MeteoWsService_Service service = new org.foi.nwtis.matlazar.ws.klijenti.MeteoWsService_Service();
        org.foi.nwtis.matlazar.ws.klijenti.MeteoWsService port = service.getMeteoWsServicePort();
        return port.dajAdresuIoTUredaja(id, korime, pass);
    }

    public static java.util.List<org.foi.nwtis.matlazar.ws.klijenti.MeteoPodaci> dajMeteoPodatkeUVremenskomIntervalu(int id, java.lang.String korime, java.lang.String pass, java.lang.String datumOd, java.lang.String datumDo) throws UnknownHostException_Exception {
        org.foi.nwtis.matlazar.ws.klijenti.MeteoWsService_Service service = new org.foi.nwtis.matlazar.ws.klijenti.MeteoWsService_Service();
        org.foi.nwtis.matlazar.ws.klijenti.MeteoWsService port = service.getMeteoWsServicePort();
        return port.dajMeteoPodatkeUVremenskomIntervalu(id, korime, pass, datumOd, datumDo);
    }

    public static java.util.List<org.foi.nwtis.matlazar.ws.klijenti.MeteoPodaci> dajZadnjeMeteoPodatkeZaUredaj(int id, java.lang.String korime, java.lang.String pass) throws UnknownHostException_Exception {
        org.foi.nwtis.matlazar.ws.klijenti.MeteoWsService_Service service = new org.foi.nwtis.matlazar.ws.klijenti.MeteoWsService_Service();
        org.foi.nwtis.matlazar.ws.klijenti.MeteoWsService port = service.getMeteoWsServicePort();
        return port.dajZadnjeMeteoPodatkeZaUredaj(id, korime, pass);
    }

    public static java.util.List<org.foi.nwtis.matlazar.ws.klijenti.MeteoPodaci> posljednihNPrognozaZaUredaj(int id, java.lang.String brPrognoza, java.lang.String korime, java.lang.String pass) throws UnknownHostException_Exception {
        org.foi.nwtis.matlazar.ws.klijenti.MeteoWsService_Service service = new org.foi.nwtis.matlazar.ws.klijenti.MeteoWsService_Service();
        org.foi.nwtis.matlazar.ws.klijenti.MeteoWsService port = service.getMeteoWsServicePort();
        return port.posljednihNPrognozaZaUredaj(id, brPrognoza, korime, pass);
    }

    public static java.util.List<org.foi.nwtis.matlazar.ws.klijenti.MeteoPodaci> vazeciPodaciZaOdabraniIoT(int id, java.lang.String korime, java.lang.String pass) throws UnknownHostException_Exception {
        org.foi.nwtis.matlazar.ws.klijenti.MeteoWsService_Service service = new org.foi.nwtis.matlazar.ws.klijenti.MeteoWsService_Service();
        org.foi.nwtis.matlazar.ws.klijenti.MeteoWsService port = service.getMeteoWsServicePort();
        return port.vazeciPodaciZaOdabraniIoT(id, korime, pass);
    }
    
    
    
}
