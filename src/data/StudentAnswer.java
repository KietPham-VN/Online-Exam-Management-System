package data;

public class StudentAnswer {

  private int studentAnswerID;
  private int studentExamID;
  private Integer selectedAnswerID; // Có thể null
  private String shortAnswer;

  // Constructor
  public StudentAnswer(int studentAnswerID, int studentExamID, Integer selectedAnswerID, String shortAnswer) {
    this.studentAnswerID = studentAnswerID;
    this.studentExamID = studentExamID;
    this.selectedAnswerID = selectedAnswerID;
    this.shortAnswer = shortAnswer;
  }

  // Getters and Setters
  public int getStudentAnswerID() {
    return studentAnswerID;
  }

  public void setStudentAnswerID(int studentAnswerID) {
    this.studentAnswerID = studentAnswerID;
  }

  public int getStudentExamID() {
    return studentExamID;
  }

  public void setStudentExamID(int studentExamID) {
    this.studentExamID = studentExamID;
  }

  public Integer getSelectedAnswerID() {
    return selectedAnswerID;
  }

  public void setSelectedAnswerID(Integer selectedAnswerID) {
    this.selectedAnswerID = selectedAnswerID;
  }

  public String getShortAnswer() {
    return shortAnswer;
  }

  public void setShortAnswer(String shortAnswer) {
    this.shortAnswer = shortAnswer;
  }
}
