package data;

import java.util.ArrayList;
import java.util.List;

public class Question {

    private int questionID;
    private String questionText;
    private String questionType;
    private int marks;
    private int examID;
    private int subjectID;
    private List<Choice> choices;

    // Constructor
    public Question(int questionID, String questionText, String questionType, int marks, int examID, int subjectID) {
        this.questionID = questionID;
        this.questionText = questionText;
        this.questionType = questionType;
        this.marks = marks;
        this.examID = examID;
        this.subjectID = subjectID;
        this.choices = new ArrayList<>();
    }

    // Getters and Setters
    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    public int getExamID() {
        return examID;
    }

    public void setExamID(int examID) {
        this.examID = examID;
    }

    public int getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(int subjectID) {
        this.subjectID = subjectID;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void addChoice(Choice choice) {
        choices.add(choice);
    }

    // Method to check if the selected answer is correct based on the choice index (e.g., 'a', 'b', etc.)
    public boolean isCorrect(String selectedAnswer) {
        int index = selectedAnswer.charAt(0) - 'a';
        if (index >= 0 && index < choices.size()) {
            return choices.get(index).isCorrect();
        }
        return false;
    }
}
