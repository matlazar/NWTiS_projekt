/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matlazar.web.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HP
 */
public class Baza {

    private Connection con;
    String bpServer;
    String bazaIme;
    String bpKorisnik;
    String bpLozinka;
    String driver;

    public Baza() {

    }

    public Baza(String bpServer, String bazaIme, String bpKorisnik, String bpLozinka, String driver) {
        this.bpServer = bpServer;
        this.bazaIme = bazaIme;
        this.bpKorisnik = bpKorisnik;
        this.bpLozinka = bpLozinka;
        this.driver = driver;
    }

    public void spojiBazu() {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Baza.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            con = DriverManager.getConnection(bpServer + bazaIme, bpKorisnik, bpLozinka);
        } catch (SQLException ex) {
            Logger.getLogger(Baza.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void zatvoriBazu() {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(Baza.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public int insertUpit(String upit) {
        int odg = 0;
        Statement statement;
        try {
            statement = con.createStatement();
            odg = statement.executeUpdate(upit);
            statement.close();
            odg = 1;
            return odg;
        } catch (SQLException ex) {
            Logger.getLogger(Baza.class.getName()).log(Level.SEVERE, null, ex);
        }
        return odg;
    }

    public ResultSet selectUpit(String upit) {
        Statement statement;
        ResultSet odg = null;
        try {
            statement = con.createStatement();
            odg = statement.executeQuery(upit);
        } catch (SQLException ex) {
            Logger.getLogger(Baza.class.getName()).log(Level.SEVERE, null, ex);
        }
        return odg;
    }

}
