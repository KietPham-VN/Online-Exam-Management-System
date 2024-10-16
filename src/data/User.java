package data;

public class User {

  private int userID;
  private String username;
  private String password;
  private String email;
  private String role;
  private java.sql.Timestamp createdAt;

  // Constructor
  public User(int userID, String username, String password, String email, String role, java.sql.Timestamp createdAt) {
    this.userID = userID;
    this.username = username;
    this.password = password;
    this.email = email;
    this.role = role;
    this.createdAt = createdAt;
  }

  // Getters and Setters
  public int getUserID() {
    return userID;
  }

  public void setUserID(int userID) {
    this.userID = userID;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public java.sql.Timestamp getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(java.sql.Timestamp createdAt) {
    this.createdAt = createdAt;
  }
}
