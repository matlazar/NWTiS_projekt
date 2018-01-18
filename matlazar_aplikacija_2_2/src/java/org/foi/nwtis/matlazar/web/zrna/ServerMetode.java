/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matlazar.web.zrna;

import com.sun.tools.ws.wsdl.document.Output;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author HP
 */
@ManagedBean
@Named(value = "serverMetode")
@SessionScoped
public class ServerMetode implements Serializable {

    private String komanda = "";
    private String odgovor = "";
    private final String provjera = "USER ([^\\s]+); PASSWD ([^\\s]+); IoT (\\d{1,6}) (ADD ([^\\s]+) ([^\\s]+)|WORK|WAIT|REMOVE|);$";
    private final String provjera2 = "USER ([^\\s]+); PASSWD ([^\\s]+); IoT_Master (LOAD|CLEAR|LIST);$";

    /**
     * Creates a new instance of ServerMetode
     */
    public ServerMetode() {
    }

    public void izvrsiKomandu() {
        InputStream is = null;
        OutputStream os = null;
        Socket socket = null;
        String nazivServer = "localhost";
        Pattern p1 = Pattern.compile(provjera);
        Pattern p2 = Pattern.compile(provjera2);
        Matcher m1 = p1.matcher(getKomanda());
        Matcher m2 = p2.matcher(getKomanda());
        boolean s1 = m1.matches();
        boolean s2 = m2.matches();

        if (s1 || s2) {
            setOdgovor("Nazalost ove komande nisu dozvoljene u ovom pogledu");
        } else {
            try {
                socket = new Socket(nazivServer, 9999);
                is = socket.getInputStream();
                os = socket.getOutputStream();
                os.write(komanda.getBytes());
                os.flush();
                socket.shutdownOutput();
                StringBuffer sb = new StringBuffer();
                while (true) {
                    int znak = is.read();
                    if (znak == -1) {
                        break;
                    }
                    sb.append((char) znak);
                }
                String povrat = sb.toString();
                setOdgovor(povrat);
                setKomanda(komanda);
            } catch (IOException ex) {
                Logger.getLogger(ServerMetode.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ServerMetode.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    public String getKomanda() {
        return komanda;
    }

    public void setKomanda(String komanda) {
        this.komanda = komanda;
    }

    public String getOdgovor() {
        return odgovor;
    }

    public void setOdgovor(String odgovor) {
        this.odgovor = odgovor;
    }

}
