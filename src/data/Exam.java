package data;

public class Exam {

  private int examID;
  private String examName;
  private int subjectID;
  private int instructorID;
  private java.sql.Date examDate;
  private int duration;
  private int totalMarks;

  // Constructor
  public Exam(int examID, String examName, int subjectID, int instructorID, java.sql.Date examDate, int duration, int totalMarks) {
    this.examID = examID;
    this.examName = examName;
    this.subjectID = subjectID;
    this.instructorID = instructorID;
    this.examDate = examDate;
    this.duration = duration;
    this.totalMarks = totalMarks;
  }

  // Getters and Setters
  public int getExamID() {
    return examID;
  }

  public void setExamID(int examID) {
    this.examID = examID;
  }

  public String getExamName() {
    return examName;
  }

  public void setExamName(String examName) {
    this.examName = examName;
  }

  public int getSubjectID() {
    return subjectID;
  }

  public void setSubjectID(int subjectID) {
    this.subjectID = subjectID;
  }

  public int getInstructorID() {
    return instructorID;
  }

  public void setInstructorID(int instructorID) {
    this.instructorID = instructorID;
  }

  public java.sql.Date getExamDate() {
    return examDate;
  }

  public void setExamDate(java.sql.Date examDate) {
    this.examDate = examDate;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public int getTotalMarks() {
    return totalMarks;
  }

  public void setTotalMarks(int totalMarks) {
    this.totalMarks = totalMarks;
  }
}
