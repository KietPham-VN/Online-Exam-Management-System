/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import runtime.App;

/**
 *
 * @author NGHIA
 */
public class database {
    private String server;
    private String user;
    private String password;
    private String db ;
    private int port;
    private SQLServerDataSource ds = new SQLServerDataSource();
    
    public database(String server,String user,String password,String db, int port){
        ds.setUser(user);
        ds.setPassword(password);
        ds.setDatabaseName(db);
        ds.setServerName(server);
        ds.setPortNumber(port);
        ds.setTrustServerCertificate(true);
    }
    
    public Connection connect() throws SQLException{
       return ds.getConnection();
    }
}
