package data;

public class StudentExam {

  private int studentExamID;
  private int examID;
  private int studentID;
  private boolean isCompleted;
  private java.sql.Timestamp startTime;
  private java.sql.Timestamp endTime;

  // Constructor
  public StudentExam(int studentExamID, int examID, int studentID, boolean isCompleted, java.sql.Timestamp startTime, java.sql.Timestamp endTime) {
    this.studentExamID = studentExamID;
    this.examID = examID;
    this.studentID = studentID;
    this.isCompleted = isCompleted;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  // Getters and Setters
  public int getStudentExamID() {
    return studentExamID;
  }

  public void setStudentExamID(int studentExamID) {
    this.studentExamID = studentExamID;
  }

  public int getExamID() {
    return examID;
  }

  public void setExamID(int examID) {
    this.examID = examID;
  }

  public int getStudentID() {
    return studentID;
  }

  public void setStudentID(int studentID) {
    this.studentID = studentID;
  }

  public boolean isCompleted() {
    return isCompleted;
  }

  public void setCompleted(boolean isCompleted) {
    this.isCompleted = isCompleted;
  }

  public java.sql.Timestamp getStartTime() {
    return startTime;
  }

  public void setStartTime(java.sql.Timestamp startTime) {
    this.startTime = startTime;
  }

  public java.sql.Timestamp getEndTime() {
    return endTime;
  }

  public void setEndTime(java.sql.Timestamp endTime) {
    this.endTime = endTime;
  }
}
