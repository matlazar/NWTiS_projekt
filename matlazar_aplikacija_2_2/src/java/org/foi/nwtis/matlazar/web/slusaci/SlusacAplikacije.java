/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matlazar.web.slusaci;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.nwtis.matlazar.konfiguracije.Konfiguracija;
import org.foi.nwtis.matlazar.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.matlazar.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.matlazar.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.matlazar.konfiguracije.bp.BP_Konfiguracija;

/**
 * Web application lifecycle listener.
 *
 * @author HP
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener {

    public static ServletContext staticContext;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        staticContext = sce.getServletContext();
        String datoteka = staticContext.getRealPath("/WEB-INF")
                + File.separator + staticContext.getInitParameter("konfiguracija");
        BP_Konfiguracija bpkonf = new BP_Konfiguracija(datoteka);
        staticContext.setAttribute("BP_Konfig", bpkonf);

        Konfiguracija konf = null;
        try {
            konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(datoteka);
        } catch (NemaKonfiguracije ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NeispravnaKonfiguracija ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }

        staticContext.setAttribute("Konfig", konf);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    public static ServletContext getStaticContext() {
        return staticContext;
    }
}
