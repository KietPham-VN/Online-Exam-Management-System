package data;

public class Subject {

  private int subjectID;
  private String subjectName;

  // Constructor
  public Subject(int subjectID, String subjectName) {
    this.subjectID = subjectID;
    this.subjectName = subjectName;
  }

  // Getters and Setters
  public int getSubjectID() {
    return subjectID;
  }

  public void setSubjectID(int subjectID) {
    this.subjectID = subjectID;
  }

  public String getSubjectName() {
    return subjectName;
  }

  public void setSubjectName(String subjectName) {
    this.subjectName = subjectName;
  }
}
