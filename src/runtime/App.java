package runtime;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import controller.CreateExamController;
import controller.DeleteController;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

  @SuppressWarnings("deprecation")
  public static void main(String[] args) throws SQLException {
    String server = "urjellyfish.mssql.somee.com";
    String user = "urjellyfish_SQLLogin_1";
    String password = "5medi3d4w6";
    String db = "urjellyfish";
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
            CreateExamController examController = new CreateExamController();
            DeleteController deleteController = new DeleteController();
            
            // Menu loop for selecting actions
            Scanner scanner = new Scanner(System.in);
            boolean keepRunning = true;
            while (keepRunning) {
                System.out.println("What would you like to do?");
                System.out.println("1. Add Exam");
                System.out.println("2. Add Question and Choices");
                System.out.println("3. Delete Question");
                System.out.println("4. Exit");
                System.out.println("4. Delete Exam");
                System.out.println("6. Delete Choices");
                System.out.print("Select an option (1-6): ");
                String option = scanner.nextLine();
                
                switch (option) {
                    case "1":
                        // Call the method to add an exam
                        examController.addExam(conn);
                        break;
                        
                    case "2":
                        // Call the method to add a question and its choices
                        examController.addQuestion(conn);
                        break;
                    case "3":
                        //Call the method to del question
                        deleteController.deleteQuestion(conn);
                        break;
                    case "4":
                        // Exit the program
                        keepRunning = false;
                        break;
                    case "5":
                        deleteController.deleteExam(conn);
                        break;
                    case "6":
                        deleteController.deleteChoice(conn);
                        break;
                    default:
                        System.out.println("Invalid option. Please choose 1, 2, or 3.");
                        break;
                }
            }

            System.out.println("Thank you for using the system!");

        } catch (SQLServerException e) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, "SQL Server error: ", e);
        } catch (SQLException e) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, "SQL error: ", e);
        }
    }
}
