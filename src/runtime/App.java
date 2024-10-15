package runtime;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

  @SuppressWarnings("deprecation")
  public static void main(String[] args) throws SQLException {
    String server = "KEITH\\SQLEXPRESS";
    String user = "sa";
    String password = "root";
    String db = "Online_Exam_Management_System";
    int port = 1433;
    SQLServerDataSource ds = new SQLServerDataSource();
    ds.setUser(user);
    ds.setPassword(password);
    ds.setDatabaseName(db);
    ds.setServerName(server);
    ds.setPortNumber(port);
    ds.setEncrypt(false);
    ds.setTrustServerCertificate(true);

    try {
      Connection conn = ds.getConnection();
      Statement stmt = null;
      try {
        stmt = conn.createStatement();
      } catch (SQLException ex) {
        Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
      }

      // Truy vấn SELECT
      String sql = "SELECT UserID, Username, Email, Role, CreatedAt FROM tbl_Users";
      ResultSet rs = stmt.executeQuery(sql);

      // Duyệt qua kết quả và in ra
      while (rs.next()) {
        System.out.println("UserID: " + rs.getInt("UserID") + ", Username: " + rs.getString("Username") + 
                           ", Email: " + rs.getString("Email") + ", Role: " + rs.getString("Role") +
                           ", CreatedAt: " + rs.getTimestamp("CreatedAt"));
      }
    } catch (SQLServerException e) {
      System.out.println(e);
    }
  }
}
