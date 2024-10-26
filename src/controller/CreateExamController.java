/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import controller.ValidationMarks;
import data.ExamData;
import ui.ExamMenu;

public class CreateExamController {
    private final InputHandler inputHandler;
    
    //Pass in the exam model here
    public CreateExamController() {
        this.inputHandler = new InputHandler();
    }

    public void addExam(Connection conn) throws SQLException {
        String insertExam = "INSERT INTO tbl_Exams (ExamName, SubjectID, InstructorID, ExamDate, Duration, TotalMarks) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement examStmt = conn.prepareStatement(insertExam, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ExamMenu examMenu = new ExamMenu();
            
            while (true) {
                // Get exam data from menu
                ExamData examData = examMenu.examNameMenu(conn);
                examStmt.setString(1, examData.getExamName());
                examStmt.setInt(2, examData.getSubjectID());
                
                // Get instructor ID
                int instructorID = inputHandler.getInstructorID(conn);
                examStmt.setInt(3, instructorID);
                
                // Get and set exam date
                examStmt.setDate(4, inputHandler.getExamDate());
                
                // Get and set duration
                examStmt.setInt(5, inputHandler.getExamDuration());
                
                // Get and set total marks
                examStmt.setInt(6, inputHandler.getExamMark());
                
                // Execute update
                examStmt.executeUpdate();
                System.out.println("Exam added successfully");
                
                if (!inputHandler.continueAddingExams()) {
                    return;
                }
            }
        }
    }

    //Thêm câu hỏi
    public void addQuestion(Connection conn) throws SQLException {
        String insertQuestion = "INSERT INTO tbl_Questions (QuestionText, QuestionType, Marks, ExamID, SubjectID) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement questionStmt = conn.prepareStatement(insertQuestion, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ValidationMarks validation = new ValidationMarks();
            
            // Get question data
            String questionText = inputHandler.getQuestionText();
            String questionType = inputHandler.getQuestionType();
            int examID = inputHandler.getExamID(conn);
            int subjectID = inputHandler.getSubjectID(conn);
            int marks = inputHandler.getQuestionMarks(conn, examID, validation);
            
            // Set parameters
            questionStmt.setString(1, questionText);
            questionStmt.setString(2, questionType);
            questionStmt.setDouble(3, marks);
            questionStmt.setInt(4, examID);
            questionStmt.setInt(5, subjectID);
            
            // Execute update
            questionStmt.executeUpdate();
            System.out.println("Question added successfully.");
            
            // Get generated ID and add choices
            try (ResultSet generatedKeys = questionStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int questionID = generatedKeys.getInt(1);
                    System.out.println("Generated Question ID: " + questionID);
                    addChoices(conn, questionID);
                }
            }
        }
    }

    public void addChoices(Connection conn, int questionID) throws SQLException {
        String insertChoice = "INSERT INTO tbl_Choices (QuestionID, ChoiceText, IsCorrect) VALUES (?, ?, ?)";

        try (PreparedStatement choiceStmt = conn.prepareStatement(insertChoice)) {
            while (true) {
                String choiceText = inputHandler.getChoiceText();
                boolean isCorrect = inputHandler.isCorrectAnswer();
                
                choiceStmt.setInt(1, questionID);
                choiceStmt.setString(2, choiceText);
                choiceStmt.setBoolean(3, isCorrect);
                
                choiceStmt.executeUpdate();
                System.out.println("Choice added successfully.");
                
                if (!inputHandler.continueAddingChoices()) {
                    break;
                }
            }
        }
    }
}
