/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.sql.Connection;
import java.sql.SQLException;
import controller.ValidationMarks;
import data.Exam;
import data.Question;
import repository.ExamRepository;
import ui.ExamMenu;
import ui.Menu;
import utils.Inputter;

public class CreateExamController {

    private final ExamRepository examRepository;
    private final ValidationMarks validMarks;

    //Pass in the exam model here
    public CreateExamController() {
        this.examRepository = new ExamRepository();
        this.validMarks = new ValidationMarks();
    }

    public void addExam(Connection conn) throws SQLException {
        ExamMenu examMenu = new ExamMenu();
        boolean choice = true;
        do {
            Exam newExam = examMenu.examNameMenu(conn);
            examRepository.insertExam(conn, newExam);
            System.out.println("Exam added successfully.");
            choice = Menu.isContinue("Do you want to add another exam?[Y/N]: ");
        } while (choice == true);
    }

    //Thêm câu hỏi
    public void addQuestion(Connection conn) throws SQLException {
        while (true) {
        String questionText = Inputter.getString("Enter the question text: ", "Question cannot be empty");
        String questionType = Inputter.getString("Enter question type [MCQ|ShortAnswer]: ", "Please enter MCQ or ShortAnswer", "^(MCQ|ShortAnswer)$");

        int examID;
        int subjectID;
        int marks;
        boolean isExamValid;
        boolean isSubjectValid;
        boolean isValidMark;

        // Validate Exam ID
        do {
            examID = Inputter.getAnInteger("Enter examID: ", "ExamID cannot be empty");
            isExamValid = examRepository.checkExamExists(conn, examID);
            if (!isExamValid) {
                System.out.println("Exam not found");
            }
        } while (!isExamValid);

        // Validate Subject ID
        do {
            subjectID = Inputter.getAnInteger("Enter subject ID: ", "SubjectID cannot be empty");
            isSubjectValid = examRepository.checkSubjectExists(conn, subjectID);
            if (!isSubjectValid) {
                System.out.println("Subject not found");
            }
        } while (!isSubjectValid);

        // Validate Marks with available exam mark constraint
        do {
            marks = Inputter.getAnInteger("Enter question marks: ", "Invalid marks", 1, 10);
            isValidMark = validMarks.validateQuestionMark(conn, examID, marks);
            if (!isValidMark) {
                System.out.println("The entered marks exceed the remaining available marks. Please enter a valid mark.");
            }
        } while (!isValidMark);

        // Create and insert the Question
        Question questionData = new Question(0, questionText, questionType, marks, examID, subjectID);
        examRepository.insertQuestion(conn, examID, questionData);
        System.out.println("Question added successfully.");

        // Add choices for the question if needed
        addChoices(conn, questionData.getQuestionID());

        // Option to continue adding questions
        boolean continueAdding = Menu.isContinue("Do you want to add another question?[Y/N]: ");
        if (!continueAdding) {
            break;
        }
    }
}

    public void addChoices(Connection conn, int questionID) throws SQLException {
        boolean isQuestionValid = examRepository.checkQuestionExists(conn, questionID);
        if (!isQuestionValid) {
            System.out.println("Question ID not found.");
            return;
        }
        // Allow adding multiple choices for a question
        while (true) {
            String choiceText = Inputter.getString("Enter choice text: ", "Choice cannot be empty");
            boolean isCorrect = Menu.isContinue("Is this choice correct? [Y/N]: ");

            // Insert the choice into the database
            examRepository.insertChoice(conn, questionID, choiceText, isCorrect);
            System.out.println("Choice added successfully.");

            // Check if the user wants to continue adding choices
            boolean continueAdding = Menu.isContinue("Do you want to add another choice?[Y/N]: ");
            if (!continueAdding) {
                break;
            }
        }
    }
}
