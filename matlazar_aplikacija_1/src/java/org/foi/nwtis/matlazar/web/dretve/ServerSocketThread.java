/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matlazar.web.dretve;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.handler.MessageContext;
import org.foi.nwtis.matlazar.konfiguracije.Konfiguracija;
import org.foi.nwtis.matlazar.konfiguracije.bp.BP_Konfiguracija;
import static org.foi.nwtis.matlazar.web.dretve.PreuzimajMeteoPodatke.pause;
import org.foi.nwtis.matlazar.web.podaci.Baza;
import org.foi.nwtis.matlazar.ws.klijenti.IoTMaster;
import org.foi.nwtis.matlazar.ws.serveri.Uredjaj;

/**
 *
 * @author HP
 */
public class ServerSocketThread extends Thread {

    private ServletContext sc = null;
    ServerSocket serverSocket;
    Socket socket;
    BP_Konfiguracija bpkonf;
    Konfiguracija konf;
    public InputStream is = null;
    public OutputStream os = null;
    Baza baza;
    String ioT = "IoT ";
    private final String serverSocketKomande = "USER ([^\\s]+); PASSWD ([^\\s]+); (START|STOP|STAT|PAUSE);$";
    private final String iotMasterkomande = "USER ([^\\s]+); PASSWD ([^\\s]+); IoT_Master (START|STOP|WORK|WAIT|LOAD|CLEAR|STATUS|LIST);$";
    private final String iotKomande = "USER ([^\\s]+); PASSWD ([^\\s]+); IoT (\\d{1,6}) (ADD ([^\\s]+) ([^\\s]+)|WORK|WAIT|REMOVE|STATUS);$";

    public void setSc(ServletContext sc) {
        this.sc = sc;
    }

    @Override
    public void interrupt() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerSocketThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        String url = "/matlazar_aplikacija_1";
        long pocetnoVrijeme = System.currentTimeMillis();
        konf = (Konfiguracija) sc.getAttribute("Konfig");
        bpkonf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");
        int port = Integer.parseInt(konf.dajPostavku("server.port"));
        String bpServer = konf.dajPostavku("server.database");
        String bazaIme = konf.dajPostavku("user.database");
        String bpKorisnik = konf.dajPostavku("user.username");
        String bpLozinka = konf.dajPostavku("user.password");
        String driver = bpkonf.getDriverDatabase();
        baza = new Baza(bpServer, bazaIme, bpKorisnik, bpLozinka, driver);
        try {
            serverSocket = new ServerSocket(port);
            Pattern pattern_1 = Pattern.compile(serverSocketKomande);
            Pattern pattern_2 = Pattern.compile(iotMasterkomande);
            Pattern pattern_3 = Pattern.compile(iotKomande);
            while (true) {
                socket = serverSocket.accept();
                String ipAdresa = socket.getRemoteSocketAddress().toString();
                is = socket.getInputStream();
                os = socket.getOutputStream();
                StringBuffer sb = new StringBuffer();
                while (true) {
                    int znak = is.read();
                    if (znak == -1) {
                        break;
                    }
                    sb.append((char) znak);
                }
                System.out.println(sb);

                Matcher m_1 = pattern_1.matcher(sb);
                Matcher m_2 = pattern_2.matcher(sb);
                Matcher m_3 = pattern_3.matcher(sb);

                boolean serverKomanda = m_1.matches();
                boolean masterKomanda = m_2.matches();
                boolean iotKomanda = m_3.matches();

                if (serverKomanda) {
                    String korIme = m_1.group(1);
                    String pass = m_1.group(2);
                    String komanda = m_1.group(3);
                    switch (komanda) {
                        case "PAUSE":
                            serverPause(korIme, pass, url, ipAdresa);
                            socket.shutdownOutput();
                            break;
                        case "START":
                            serverStart(korIme, pass, url, ipAdresa);
                            socket.shutdownOutput();
                            break;
                        case "STOP":
                            serverStop(korIme, pass, url, ipAdresa);
                            socket.shutdownOutput();
                            break;
                        case "STAT":
                            serverStat(korIme, pass, url, ipAdresa);
                            socket.shutdownOutput();
                            break;
                        default:
                            os.write("Kriva naredba".getBytes());
                            os.flush();
                            socket.shutdownOutput();
                            break;
                    }
                } else if (masterKomanda) {
                    String korIme = m_2.group(1);
                    String pass = m_2.group(2);
                    String komanda = m_2.group(3);
                    if (IoTMaster.autenticirajGrupuIoT(korIme, pass)) {
                        switch (komanda) {
                            case "START":
                                ioTMaserStart(korIme, pass, url, ipAdresa);
                                socket.shutdownOutput();
                                break;
                            case "STOP":
                                ioTMasterStop(korIme, pass, url, ipAdresa);
                                socket.shutdownOutput();
                                break;
                            case "WORK":
                                iotMasterWork(korIme, pass, url, ipAdresa);
                                socket.shutdownOutput();
                                break;
                            case "WAIT":
                                ioTMasterWait(korIme, pass, url, ipAdresa);
                                socket.shutdownOutput();
                                break;
                            case "LOAD":
                                ioTMasterLoad(korIme, pass, url, ipAdresa);
                                socket.shutdownOutput();
                                break;
                            case "CLEAR":
                                ioTMasterClear(korIme, pass, url, ipAdresa);
                                socket.shutdownOutput();
                                break;
                            case "STATUS":
                                ioTMasterStatus(korIme, pass, url, ipAdresa);
                                socket.shutdownOutput();
                                break;
                            case "LIST":
                                ioTMasterList(korIme, pass, url, ipAdresa);
                                socket.shutdownOutput();
                                break;
                            default:
                                os.write("Kriva naredba".getBytes());
                                os.flush();
                                socket.shutdownOutput();
                                break;
                        }
                    } else {
                        os.write("ERR 10: Kriva lozinka ili korisničko ime".getBytes());
                        os.flush();
                        socket.shutdownOutput();
                    }
                } else if (iotKomanda) {
                    String korIme = m_3.group(1);
                    String pass = m_3.group(2);
                    String idIoT = m_3.group(3);
                    String komanda = m_3.group(4);
                    if (IoTMaster.autenticirajGrupuIoT(korIme, pass)) {
                        if (komanda.substring(0, 3).equals("ADD")) {
                            //todo obradi ADD komandu
                            String naziv = m_3.group(5);
                            String adresa = m_3.group(6);
                            ioTAdd(korIme, pass, Integer.parseInt(idIoT), naziv, adresa);
                            socket.shutdownOutput();
                            
                        } else {
                            switch (komanda) {
                                case "WORK":
                                    ioTWork(korIme, pass, Integer.parseInt(idIoT));
                                    socket.shutdownOutput();
                                    break;
                                case "WAIT":
                                    ioTWait(korIme, pass, Integer.parseInt(idIoT));
                                    socket.shutdownOutput();
                                    break;
                                case "REMOVE":
                                    ioTRemove(korIme, pass, Integer.parseInt(idIoT));
                                    socket.shutdownOutput();
                                    break;
                                case "STATUS":
                                    iotStatus(korIme, pass, Integer.parseInt(idIoT));
                                    socket.shutdownOutput();
                                    break;
                                default:
                                    os.write("Kriva naredba".getBytes());
                                    os.flush();
                                    socket.shutdownOutput();
                                    break;
                            }
                        }
                    } else {
                        os.write("ERR 10: Kriva lozinka ili korisničko ime".getBytes());
                        os.flush();
                        socket.shutdownOutput();
                    }
                } else {
                    System.out.println("Vrtim");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerSocketThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    public boolean autentikacija(String korIme, String pass) {
        boolean provjera = false;
        try {
            baza.spojiBazu();
            String upit = "SELECT * FROM korisnik WHERE korisnickoIme = '" + korIme + "' AND Lozinka = '" + pass + "'";
            ResultSet rs = baza.selectUpit(upit);
            if (rs.next()) {
                provjera = true;
            }
            baza.zatvoriBazu();
        } catch (SQLException ex) {
            Logger.getLogger(ServerSocketThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        return provjera;
    }

    private void serverPause(String korIme, String pass, String url, String ip) throws IOException {
        long pocetak = System.currentTimeMillis();
        if (!autentikacija(korIme, pass)) {
            os.write("ERR 10: Pogresno korisnicko ime ili lozinka".getBytes());
            os.flush();
        }
        if (!PreuzimajMeteoPodatke.pause) {
            os.write("ERR 10: Sustav je već u stanju PAUSE".getBytes());
            os.flush();
        }
        if (autentikacija(korIme, pass) && PreuzimajMeteoPodatke.pause) {
            PreuzimajMeteoPodatke.pause = false;
            os.write("OK 10: Sustav ste pauzirali".getBytes());
            os.flush();
        }
        int razlika = (int) (System.currentTimeMillis() - pocetak) / 1000;
        dodajUDnevnik(korIme, url, ip, razlika, 11);
    }

    private void serverStart(String korIme, String pass, String url, String ip) throws IOException {
        long pocetak = System.currentTimeMillis();
        if (!autentikacija(korIme, pass)) {
            os.write("ERR 10: Pogresno korisnicko ime ili lozinka".getBytes());
            os.flush();
        }
        if (PreuzimajMeteoPodatke.pause) {
            os.write("ERR 11: Sustav nije ni bio u stanju PAUSE".getBytes());
            os.flush();
        }
        if (autentikacija(korIme, pass) && !PreuzimajMeteoPodatke.pause) {
            PreuzimajMeteoPodatke.pause = true;
            os.write("OK 11: Pokrenuli ste sustav".getBytes());
            os.flush();
        }
        int razlika = (int) (System.currentTimeMillis() - pocetak) / 1000;
        dodajUDnevnik(korIme, url, ip, razlika, 12);
    }

    private void serverStop(String korIme, String pass, String url, String ip) throws IOException {
        long pocetak = System.currentTimeMillis();
        if (!autentikacija(korIme, pass)) {
            os.write("ERR 10: Pogresno korisnicko ime ili lozinka".getBytes());
            os.flush();
        }
        if (!PreuzimajMeteoPodatke.stop) {
            os.write("ERR 12: Sustav je već u postupku STOP".getBytes());
            os.flush();
        }
        if (autentikacija(korIme, pass) && PreuzimajMeteoPodatke.stop) {
            PreuzimajMeteoPodatke.stop = false;
            os.write("OK 10: Sustav ste zaustavili".getBytes());
            os.flush();
            os.close();
            is.close();
        }
        int razlika = (int) (System.currentTimeMillis() - pocetak) / 1000;
        dodajUDnevnik(korIme, url, ip, razlika, 13);
    }

    private void serverStat(String korIme, String pass, String url, String ip) throws IOException {
        long pocetak = System.currentTimeMillis();
        if (!autentikacija(korIme, pass)) {
            os.write("ERR 10: Pogresno korisnicko ime ili lozinka".getBytes());
            os.flush();
        }
        if (PreuzimajMeteoPodatke.pause && PreuzimajMeteoPodatke.stop) {
            os.write("OK 14: Preuzimam podatke".getBytes());
            os.flush();
        }
        if (PreuzimajMeteoPodatke.stop && !PreuzimajMeteoPodatke.pause) {
            os.write("OK 13: Privremeno ne preuzimam podatke".getBytes());
            os.flush();
        }

        if (PreuzimajMeteoPodatke.stop && !PreuzimajMeteoPodatke.pause) {
            os.write("OK 15: Ne preuzimam podatke i korisnicke komande".getBytes());
            os.flush();
        }
        int razlika = (int) (System.currentTimeMillis() - pocetak) / 1000;
        dodajUDnevnik(korIme, url, ip, razlika, 14);
    }

    private void ioTMaserStart(String korIme, String pass, String url, String ip) throws IOException {
        long pocetak = System.currentTimeMillis();
        if (IoTMaster.dajStatusGrupeIoT(korIme, pass).toString().equals("AKTIVAN")) {
            os.write("ERR 20: Grupa je već registrirana".getBytes());
            os.flush();
        } else if (IoTMaster.registrirajGrupuIoT(korIme, pass)) {
            os.write("OK 10: Grupa je uspješno registrirana".getBytes());
            os.flush();
        } else {
            os.write("Neuspješna registracija grupe".getBytes());
            os.flush();
        }
        int razlika = (int) (System.currentTimeMillis() - pocetak) / 1000;
        dodajUDnevnik(korIme, url, ip, razlika, 21);
    }

    private void ioTMasterStop(String korIme, String pass, String url, String ip) throws IOException {
        long pocetak = System.currentTimeMillis();
        if (IoTMaster.dajStatusGrupeIoT(korIme, pass).toString().equals("BLOKIRAN")) {
            os.write("ERR 21: Grupa je već deregistrirana".getBytes());
            os.flush();
        } else if (IoTMaster.deregistrirajGrupuIoT(korIme, pass)) {
            os.write("OK 10: Grupa je uspješno deregistrirana".getBytes());
            os.flush();
        } else {
            os.write("Neuspješna deregistracija grupe".getBytes());
            os.flush();
        }
        int razlika = (int) (System.currentTimeMillis() - pocetak) / 1000;
        dodajUDnevnik(korIme, url, ip, razlika, 22);
    }

    private void iotMasterWork(String korIme, String pass, String url, String ip) throws IOException {
        long pocetak = System.currentTimeMillis();
        if (IoTMaster.aktivirajGrupuIoT(korIme, pass)) {
            os.write("OK 10: Grupa je uspješno aktivirana".getBytes());
            os.flush();
        } else {
            os.write("ERR 22: Neuspješna aktivacija grupe".getBytes());
            os.flush();
        }
        int razlika = (int) (System.currentTimeMillis() - pocetak) / 1000;
        dodajUDnevnik(korIme, url, ip, razlika, 23);
    }

    private void ioTMasterWait(String korIme, String pass, String url, String ip) throws IOException {
        long pocetak = System.currentTimeMillis();
        if (IoTMaster.aktivirajGrupuIoT(korIme, pass)) {
            os.write("OK 10: Grupa je uspješno blokirana".getBytes());
            os.flush();
        } else {
            os.write("ERR 23: Neuspješno blokiranje grupe".getBytes());
            os.flush();
        }
        int razlika = (int) (System.currentTimeMillis() - pocetak) / 1000;
        dodajUDnevnik(korIme, url, ip, razlika, 24);
    }

    private void ioTMasterLoad(String korIme, String pass, String url, String ip) throws IOException {
        long pocetak = System.currentTimeMillis();
        if (IoTMaster.ucitajSveUredjajeGrupe(korIme, pass)) {
            os.write("OK 10: Učitani predefinirani uređaji".getBytes());
            os.flush();
        } else {
            os.write("Greška kod učitavanja".getBytes());
            os.flush();
        }
        int razlika = (int) (System.currentTimeMillis() - pocetak) / 1000;
        dodajUDnevnik(korIme, url, ip, razlika, 25);
    }

    private void ioTMasterClear(String korIme, String pass, String url, String ip) throws IOException {
        long pocetak = System.currentTimeMillis();
        if (IoTMaster.obrisiSveUredjajeGrupe(korIme, pass)) {
            os.write("OK 10: Obrisani su svi uređaji".getBytes());
            os.flush();
        } else {
            os.write("Greška kod brisanja".getBytes());
        }
        int razlika = (int) (System.currentTimeMillis() - pocetak) / 1000;
        dodajUDnevnik(korIme, url, ip, razlika, 26);
    }

    private void ioTMasterStatus(String korIme, String pass, String url, String ip) throws IOException {
        long pocetak = System.currentTimeMillis();
        if (IoTMaster.dajStatusGrupeIoT(korIme, pass).toString().equals("BLOKIRAN")) {
            os.write("OK 24: blokirana".getBytes());
            os.flush();
        } else if (IoTMaster.dajStatusGrupeIoT(korIme, pass).toString().equals("AKTIVAN")) {
            os.write("OK 25: aktivna".getBytes());
            os.flush();
        } else {
            os.write("Greška kod dohvaćanja statusa".getBytes());
            os.flush();
        }
        int razlika = (int) (System.currentTimeMillis() - pocetak) / 1000;
        dodajUDnevnik(korIme, url, ip, razlika, 27);
    }

    private void ioTMasterList(String korIme, String pass, String url, String ip) throws IOException {
        long pocetak = System.currentTimeMillis();
        List<Uredjaj> uredaj = new ArrayList<>();
        uredaj = IoTMaster.dajSveUredjajeGrupe(korIme, pass);
        StringBuffer buffer = new StringBuffer();
        for (Uredjaj u : uredaj) {
            String naziv = u.getNaziv();
            String id = Integer.toString(u.getId());
            String jedanUredaj = "," + ioT + " " + id + " " + naziv;
            buffer.append(jedanUredaj);
        }
        os.write(buffer.toString().getBytes());
        os.flush();
        int razlika = (int) (System.currentTimeMillis() - pocetak) / 1000;
        dodajUDnevnik(korIme, url, ip, razlika, 28);
    }

    private void ioTAdd(String korIme, String pass, int id, String naziv, String adresa) throws IOException {
        if (IoTMaster.dodajNoviUredjajGrupi(korIme, pass, id, naziv, adresa)) {
            os.write("OK 10: Uređaj je dodan".getBytes());
            os.flush();
        } else {
            os.write("ERR 30: Uređaj nije dodan, id postoji već".getBytes());
            os.flush();
        }
    }

    private void ioTWork(String korIme, String pass, int id) throws IOException {
        if (IoTMaster.dajStatusUredjajaGrupe(korIme, pass, id).toString().equals("AKTIVAN")) {
            os.write("ERR 31: Uređaj je već aktivan".getBytes());
            os.flush();
        } else if (IoTMaster.aktivirajUredjajGrupe(korIme, pass, id)) {
            os.write("OK 10: Uređaj je aktiviran".getBytes());
            os.flush();
        } else {
            os.write("Greška kod aktivacije".getBytes());
            os.flush();
        }
    }

    private void ioTWait(String korIme, String pass, int id) throws IOException {
        if (IoTMaster.dajStatusUredjajaGrupe(korIme, pass, id).toString().equals("BLOKIRAN")) {
            os.write("ERR 32: Uređaj je već blokiran".getBytes());
            os.flush();
        } else if (IoTMaster.blokirajUredjajGrupe(korIme, pass, id)) {
            os.write("OK 10: Uređaj je blokiran".getBytes());
            os.flush();
        } else {
            os.write("Greška kod aktivacije".getBytes());
            os.flush();
        }
    }

    private void ioTRemove(String korIme, String pass, int id) throws IOException {
        if (IoTMaster.obrisiUredjajGrupe(korIme, pass, id)) {
            os.write("OK 10: Uređaj je obrisan".getBytes());
            os.flush();
        } else {
            os.write("ERR 33: Brisanje nepostojeće uređaja, krivi id".getBytes());
            os.flush();
        }
    }

    private void iotStatus(String korIme, String pass, int id) throws IOException {
        if (IoTMaster.dajStatusUredjajaGrupe(korIme, pass, id).toString().equals("BLOKIRAN")) {
            os.write("OK 34: blokirana".getBytes());
            os.flush();
        } else if (IoTMaster.dajStatusUredjajaGrupe(korIme, pass, id).toString().equals("AKTIVAN")) {
            os.write("OK 35: aktivna".getBytes());
            os.flush();
        } else {
            os.write("Greška kod dohvaćanja statusa".getBytes());
            os.flush();
        }
    }

    public int dodajUDnevnik(String korisnik, String url, String ip, int trajanje, int status) {
        int uspjeh = 0;
        baza.spojiBazu();
        String upit = "INSERT INTO dnevnik(default, '" + korisnik + "','" + url + "','" + ip + "',default," + trajanje + "," + status + ")";
        uspjeh = baza.insertUpit(upit);
        baza.zatvoriBazu();
        baza.zatvoriBazu();
        return uspjeh;
    }
}
