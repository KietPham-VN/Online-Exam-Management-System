package runtime;

import db.database;
import java.sql.SQLException;

public class App {

  @SuppressWarnings("deprecation")
  public static void main(String[] args) {
    String server = "urjellyfish.mssql.somee.com";
    String user = "urjellyfish_SQLLogin_1";
    String password = "5medi3d4w6";
    String db = "urjellyfish";
    int port = 1433;
    database ds = new database(server,user,password,db,port);
    
    try{
        ds.connect();
    }
    catch(Exception ex){
        System.out.println(ex);
    }
 
  }
}
