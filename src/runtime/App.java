package runtime;

import db.database;
import interfaces.repository.IUserRepository;
import java.sql.Connection;
import repository.UserRepository;

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
        Connection conn = ds.connect();
        //Testing the user
        IUserRepository userRepo = new UserRepository(conn);
        System.out.println(userRepo.CreateUser("N", "123", "s@gmail.com", "Student").toString());
    }
    catch(Exception ex){
        System.out.println(ex);
    }
 
  }
}
