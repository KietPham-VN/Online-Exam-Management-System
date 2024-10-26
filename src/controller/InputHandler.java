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
import java.sql.Date;
import ui.Menu;
import utils.Inputter;

/**
 *
 * @author Hoang Tran
 */
public class InputHandler {
    //input exam
    public String getExamName() {
        return Inputter.getString("Enter the exam name: ", "Exam name cannot be empty");
    }

    public int getInstructorID(Connection conn) throws SQLException {
        String checkInstructor = "SELECT COUNT(*) FROM tbl_Teach WHERE InstructorID = ?";
        
        try (PreparedStatement checkStmt = conn.prepareStatement(checkInstructor)) {
            int instructorID;
            while (true) {
                instructorID = Inputter.getAnInteger("Enter the instructor ID of the exam: ", "InstructorID must be an integer");
                checkStmt.setInt(1, instructorID);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        break;
                    } else {
                        System.out.println("Invalid instructor ID.");
                    }
                }
            }
            return instructorID;
        }
    }
    
    public Date getExamDate() {
        while (true) {
            try {
                String dateString = Inputter.getString("Enter the exam date (dd-MM-yyyy): ", "Date cannot be empty");
                java.util.Date utilDate = new java.text.SimpleDateFormat("dd-MM-yyyy").parse(dateString);
                return new Date(utilDate.getTime());
            } catch (java.text.ParseException e) {
                System.out.println("Invalid date format. Please use 'dd-MM-yyyy'.");
            }
        }
    }
    
    public int getExamDuration() {
        return Inputter.getAnIntegerWithLowerBound("Enter the duration of the exam (minutes): ", 
                "Duration must be a positive integer", 1);
    }
    
    public int getExamMark() {
        return Inputter.getAnIntegerWithLowerBound("Enter the mark for the exam: ", 
                "Mark must be a positive integer", 1);
    }
    
    // Question Input Methods
    public String getQuestionText() {
        return Inputter.getString("Enter question text: ", "Question cannot be empty");
    }
    
    public String getQuestionType() {
        String questionType;
        do {
            questionType = Inputter.getString("Enter question type (MCQ/ShortAnswer): ", 
                    "Question type cannot be empty");
            if (questionType.equalsIgnoreCase("MCQ") || questionType.equalsIgnoreCase("ShortAnswer")) {
                break;
            }
            System.out.println("Invalid question type. Please enter either 'MCQ' or 'ShortAnswer'.");
        } while (true);
        return questionType.toUpperCase();
    }
    
    public int getExamID(Connection conn) throws SQLException {
        String checkExamID = "SELECT COUNT(*) FROM tbl_Exams WHERE ExamID = ?";
        try (PreparedStatement checkExamStmt = conn.prepareStatement(checkExamID)) {
            while (true) {
                int examID = Inputter.getAnInteger("Enter exam ID: ", "Invalid number");
                checkExamStmt.setInt(1, examID);
                try (ResultSet rs = checkExamStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return examID;
                    }
                    System.out.println("Invalid exam ID. Please enter valid exam ID");
                }
            }
        }
    }
    
    public int getSubjectID(Connection conn) throws SQLException {
        String checkSubjectID = "SELECT COUNT(*) FROM tbl_Subjects WHERE SubjectID = ?";
        try (PreparedStatement checkSubjectStmt = conn.prepareStatement(checkSubjectID)) {
            while (true) {
                int subjectID = Inputter.getAnInteger("Enter subject ID: ", "Invalid number");
                checkSubjectStmt.setInt(1, subjectID);
                try (ResultSet rs = checkSubjectStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return subjectID;
                    }
                    System.out.println("Invalid subject ID. Please enter valid subject ID");
                }
            }
        }
    }
    
    public int getQuestionMarks(Connection conn, int examID, ValidationMarks validation) throws SQLException {
        while (true) {
            int marks = Inputter.getAnIntegerWithLowerBound("Enter question marks: ", "Invalid number", 1);
            if (validation.validateQuestionMark(conn, examID, marks)) {
                return marks;
            }
        }
    }
    
    // Choice Input Methods
    public String getChoiceText() {
        return Inputter.getString("Enter choice text: ", "Choice cannot be empty");
    }
    
    public boolean isCorrectAnswer() {
        return Menu.isContinue("Is this the correct answer (yes/no)? ");
    }
    
    public boolean continueAddingChoices() {
        return Menu.isContinue("Do you want to add more choices for this question (yes/no)?");
    }
    
    public boolean continueAddingExams() {
        return Menu.isContinue("Do you want to create more exams (y/n)?");
    }
}
