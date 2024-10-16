package data;

public class Teach {

  private int instructorID;
  private int subjectID;

  // Constructor
  public Teach(int instructorID, int subjectID) {
    this.instructorID = instructorID;
    this.subjectID = subjectID;
  }

  // Getters and Setters
  public int getInstructorID() {
    return instructorID;
  }

  public void setInstructorID(int instructorID) {
    this.instructorID = instructorID;
  }

  public int getSubjectID() {
    return subjectID;
  }

  public void setSubjectID(int subjectID) {
    this.subjectID = subjectID;
  }
}
