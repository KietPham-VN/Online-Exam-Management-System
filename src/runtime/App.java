package runtime;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Connection;
import ui.StartupMenu;

public class App {

    @SuppressWarnings("deprecation")
    public static void main(String[] args) {
        String server = "Online-Exam-Management-System.mssql.somee.com";
        String user = "urjellyfish_SQLLogin_1";
        String password = "5medi3d4w6";
        String db = "Online-Exam-Management-System";
        int port = 1433;
        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setUser(user);
        ds.setPassword(password);
        ds.setDatabaseName(db);
        ds.setServerName(server);
        ds.setPortNumber(port);
        ds.setEncrypt(false);
        ds.setTrustServerCertificate(true);

        try (Connection conn = ds.getConnection()) {
            // Instantiate controllers
            StartupMenu startMenu = new StartupMenu();

            // Menu loop for selecting actions
            Scanner scanner = new Scanner(System.in);
            boolean keepRunning = true;
            while (keepRunning) {
                System.out.println("*****WELCOME TO ONLINE EXAM MANAGEMENT*****");
                System.out.println("Please login to continue");
                String option = scanner.nextLine();
                
            }

            System.out.println("Thank you for using the system!");

        } catch (SQLServerException e) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, "SQL Server error: ", e);
        } catch (SQLException e) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, "SQL error: ", e);
        }
    }

}
