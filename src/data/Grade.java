package data;

public class Grade {

  private int gradeID;
  private int studentExamID;
  private int totalScore;
  private java.sql.Timestamp gradedAt;

  // Constructor
  public Grade(int gradeID, int studentExamID, int totalScore, java.sql.Timestamp gradedAt) {
    this.gradeID = gradeID;
    this.studentExamID = studentExamID;
    this.totalScore = totalScore;
    this.gradedAt = gradedAt;
  }

  // Getters and Setters
  public int getGradeID() {
    return gradeID;
  }

  public void setGradeID(int gradeID) {
    this.gradeID = gradeID;
  }

  public int getStudentExamID() {
    return studentExamID;
  }

  public void setStudentExamID(int studentExamID) {
    this.studentExamID = studentExamID;
  }

  public int getTotalScore() {
    return totalScore;
  }

  public void setTotalScore(int totalScore) {
    this.totalScore = totalScore;
  }

  public java.sql.Timestamp getGradedAt() {
    return gradedAt;
  }

  public void setGradedAt(java.sql.Timestamp gradedAt) {
    this.gradedAt = gradedAt;
  }
}
