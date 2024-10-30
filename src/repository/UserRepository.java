/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package repository;

import data.User;
import interfaces.repository.IUserRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author NGHIA
 */
public class UserRepository implements IUserRepository {
    private Connection conn;
    public UserRepository(Connection cn){
        this.conn = cn;
    }

    @Override
    public User CreateUser(String userName, String password, String email, String role) {
        String insertExamSQL = "INSERT INTO tbl_Users (Username, Password, Email, Role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement examStmt = conn.prepareStatement(insertExamSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
          examStmt.setString(1, userName);
          examStmt.setString(2, password);
          examStmt.setString(3, email);
          examStmt.setString(4, role);
          examStmt.executeUpdate();

          ResultSet generatedKeys = examStmt.getGeneratedKeys();
          if (generatedKeys.next()) {
            return new User(generatedKeys.getInt(1),userName,password,email,role,new Timestamp(System.currentTimeMillis()));
          }
        } catch (SQLException ex) {
            Logger.getLogger(UserRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null; // Indicate failure
    }

    @Override
    public User Login(String userName, String password) {
        String selectSql = "SELECT TOP 1 * FROM tbl_Users WHERE Username=? AND Password=?";
        try(PreparedStatement stmt = conn.prepareStatement(selectSql,PreparedStatement.RETURN_GENERATED_KEYS)){
            stmt.setString(1, userName);
            stmt.setString(2, password);
            ResultSet res=stmt.executeQuery();
            
            if (res.next()) {
                int userId = res.getInt("UserID");
                String retrievedUserName = res.getString("Username");
                String retrievedPassword = res.getString("Password");
                String email = res.getString("Email");
                String role = res.getString("Role");
                Timestamp createdAt = res.getTimestamp("CreatedAt"); 

                return new User(userId, retrievedUserName, retrievedPassword, email, role, createdAt);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public User FindUserById(int userID) {
        String selectSql = "SELECT * FROM tbl_Users WHERE ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
            stmt.setInt(1, userID);

            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                String userName = resultSet.getString("Username");
                String password = resultSet.getString("Password");
                String email = resultSet.getString("Email");
                String role = resultSet.getString("Role");
                Timestamp createdAt = resultSet.getTimestamp("CreatedAt");

                return new User(userID, userName, password, email, role, createdAt);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public ArrayList<User> FindUsers(String userName) {
        ArrayList<User> users = new ArrayList<>();
        String selectSql = (userName.isEmpty())?"SELECT * FROM tbl_Users"
                : "SELECT * FROM tbl_Users WHERE Username LIKE "+"'%"+userName+"'";

        try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                int userId = resultSet.getInt("UserID");
                String password = resultSet.getString("Password");
                String email = resultSet.getString("Email");
                String role = resultSet.getString("Role");
                Timestamp createdAt = resultSet.getTimestamp("CreatedAt");

                User user = new User(userId, userName, password, email, role, createdAt);
                users.add(user);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserRepository.class.getName()).log(Level.SEVERE, null, ex);
        }

        return users; // Return empty list if no users found
    }

    @Override
    public User FindSingleUser(String userName) {
        User user = null;
        String selectSql = "SELECT * FROM tbl_Users WHERE Username WHERE ?";

        try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
            stmt.setString(1, userName);

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                int userId = resultSet.getInt("UserID");
                String password = resultSet.getString("Password");
                String email = resultSet.getString("Email");
                String role = resultSet.getString("Role");
                Timestamp createdAt = resultSet.getTimestamp("CreatedAt");

                user = new User(userId, userName, password, email, role, createdAt);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserRepository.class.getName()).log(Level.SEVERE, null, ex);
        }

        return user; // Return empty list if no users found
    }

    @Override
    public int ResetPasword(int userID,String newPassword) {
        String updatePasswordSQL = "UPDATE tbl_Users SET Password = ? WHERE UserID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updatePasswordSQL)) {
            stmt.setString(1, newPassword);
            stmt.setInt(2, userID);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return rowsAffected; 
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;    
    }
    
}