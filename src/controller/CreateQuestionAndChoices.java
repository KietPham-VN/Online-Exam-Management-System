package controller;

import ui.Menu;
import utils.Inputter;
/**
 *
 * @author Hoang Tran
 */
public class CreateQuestionAndChoices {
    //Thêm câu hỏi
    public void addQuestion() {
        int questionID = Inputter.getAnInteger("Enter question ID: ", "Invalid number");
        String questionText = Inputter.getString("Enter question text: ", "Question cannot be empty");
        String questionType = Inputter.getString("Enter the question type: ");
        double marks = Inputter.getADouble("Enter question marks: ", "Invalid number");
        int examID = Inputter.getAnInteger("Enter exam ID: ", "Invalid number");
        int subjectID = Inputter.getAnInteger("Enter subject ID: ", "Invalid number");
        
        if(!Menu.isContinue()) return;
    }
    
    public void addChoices() {
        int choiceID = Inputter.getAnInteger("Enter choiceID: ", "Invalid number");
        int questionID = Inputter.getAnInteger("Enter question ID: ", "Invalid number");
        String choiceText = Inputter.getString("Enter choice text: ", "Choice cannot be empty");
        boolean isCorrect = false;
    }
}
