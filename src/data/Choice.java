package data;

public class Choice {

    private int choiceID;
    private int questionID;
    private String choiceText;
    private boolean isCorrect;

    // Constructor
    public Choice(int choiceID, int questionID, String choiceText, boolean isCorrect) {
        this.choiceID = choiceID;
        this.questionID = questionID;
        this.choiceText = choiceText;
        this.isCorrect = isCorrect;
    }

    // Getters and Setters
    public int getChoiceID() {
        return choiceID;
    }

    public void setChoiceID(int choiceID) {
        this.choiceID = choiceID;
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public String getChoiceText() {
        return choiceText;
    }

    public void setChoiceText(String choiceText) {
        this.choiceText = choiceText;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
}
