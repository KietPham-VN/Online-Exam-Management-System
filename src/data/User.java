package data;

public class User {

  private String userId;
  private String username;
  private String password;
  private String email;
  private String role;
  private String createAt;

  public User(String userId, String username, String password, String email,
      String role, String createAt) {
    this.userId = userId;
    this.username = username;
    this.password = password;
    this.email = email;
    this.role = role;
    this.createAt = createAt;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
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

  public String getCreateAt() {
    return createAt;
  }

  public void setCreateAt(String createAt) {
    this.createAt = createAt;
  }

}
