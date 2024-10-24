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
import ui.Menu;
import utils.Inputter;
import controller.ValidationMarks;
import data.ExamData;
import ui.ExamMenu;

public class CreateExamController {
    
    
    //Pass in the exam model here
    public CreateExamController() {
    }

    public void addExam(Connection conn) throws SQLException {
        String insertExam = "INSERT INTO tbl_Exams (ExamName, SubjectID, InstructorID, ExamDate, Duration, TotalMarks) VALUES (?, ?, ?, ?, ?, ?)";
        String checkInstructor = "SELECT COUNT(*) FROM tbl_Teach WHERE InstructorID = ?"; // check if the subject ID exists
        try (PreparedStatement examStmt = conn.prepareStatement(insertExam, PreparedStatement.RETURN_GENERATED_KEYS);
                PreparedStatement checkInstructorStmt = conn.prepareStatement(checkInstructor)) {
            ExamMenu examMenu = new ExamMenu();
            while (true) {
                ExamData examData = examMenu.examNameMenu(conn);
                String examName = examData.getExamName();
                int subjectID = examData.getSubjectID();
                
                examStmt.setString(1, examName);
                examStmt.setInt(2, subjectID);
                
                int instructorID;
                while (true) {
                    instructorID = Inputter.getAnInteger("Enter the instructorID of the exam:", "InstructorID must be an integer");
                    checkInstructorStmt.setInt(1, instructorID);
                    try (ResultSet rs = checkInstructorStmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            break;
                        } else {
                            System.out.println("Invalid instructor ID");
                        }
                    }
                }
                examStmt.setInt(3, instructorID);

                java.sql.Date sqlExamDate;
                while (true) {
                    try {
                        String dateString = Inputter.getString("Enter the exam date (dd-MM-yyyy): ", "Date cannot be empty");
                        java.util.Date utilExamDate = new java.text.SimpleDateFormat("dd-MM-yyyy").parse(dateString);
                        sqlExamDate = new java.sql.Date(utilExamDate.getTime());
                        examStmt.setDate(4, sqlExamDate);
                        break;
                    } catch (java.text.ParseException e) {
                        System.out.println("Invalid date format. Please use 'dd-MM-yyyy'.");
                    }
                }

                int duration = Inputter.getAnIntegerWithLowerBound("Enter the duration of the exam (minute): ", "Duration must be a positive integer", 1);
                examStmt.setInt(5, duration);

                int mark = Inputter.getAnIntegerWithLowerBound("Enter the mark for the exam: ", "The mark must be a positive integer", 1);
                examStmt.setInt(6, mark);

                //Add exam to database
                examStmt.executeUpdate();
                System.out.println("Exam added successfully");
                if (!Menu.isContinue("Do you want to create more exams (y/n)?")) {
                    return;
                }
            }
        }
    }

    //Thêm câu hỏi
    public void addQuestion(Connection conn) throws SQLException {
        String insertQuesion = "INSERT INTO tbl_Questions (QuestionText, QuestionType, Marks, ExamID, SubjectID) VALUES (?, ?, ?, ?, ?)";
        String checkExamID = "SELECT COUNT(*) FROM tbl_Exams WHERE ExamID = ?";
        String checkSubjectID = "SELECT COUNT(*) FROM tbl_Subjects WHERE SubjectID = ?";
        try (PreparedStatement questionStmt = conn.prepareStatement(insertQuesion, PreparedStatement.RETURN_GENERATED_KEYS);
                PreparedStatement checkExamStmt = conn.prepareStatement(checkExamID);
                PreparedStatement checkSubjectStmt = conn.prepareStatement(checkSubjectID)) {
            int examID, subjectID;
            ValidationMarks validation = new ValidationMarks();
            // Add question text
            String questionText = Inputter.getString("Enter question text: ", "Question cannot be empty");
            questionStmt.setString(1, questionText);
            //Add question type

            String questionType;
            do {
                questionType = Inputter.getString("Enter question type (MCQ/ShortAnswer): ", "Question type cannot be empty");
                if (questionType.equalsIgnoreCase("MCQ") || questionType.equalsIgnoreCase("ShortAnswer")) {
                    break;
                } else {
                    System.out.println("Invalid question type. Please enter either 'MCQ' or 'ShortAnswer'.");
                }
            } while (true);

            while (true) {
                examID = Inputter.getAnInteger("Enter exam ID: ", "Invalid number");
                checkExamStmt.setInt(1, examID);
                try (ResultSet rs = checkExamStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        break;
                    } else {
                        System.out.println("Invalid exam ID. Please enter valid exam ID");
                    }
                }
            }

            while (true) {
                subjectID = Inputter.getAnInteger("Enter subject ID: ", "Invalid number");
                checkSubjectStmt.setInt(1, subjectID);
                try (ResultSet rs = checkSubjectStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        break;
                    } else {
                        System.out.println("Invalid subject ID. Please enter valid subject ID");
                    }
                }
            }
            questionStmt.setString(2, questionType.toUpperCase());
            //Add marks for each ques
            double marks;
            while (true) {
                marks = Inputter.getADouble("Enter question marks: ", "Invalid number", 1, 100);
                if (validation.validateQuestionMark(conn, examID, marks)) {
                    break;
                }
            }

            questionStmt.setDouble(3, marks);

            questionStmt.setInt(4, examID);  // Set Exam ID here

            questionStmt.setInt(5, subjectID);  // Set Subject ID here
            questionStmt.executeUpdate();
            System.out.println("Question added successfully.");
            try (ResultSet generatedKeys = questionStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int questionID = generatedKeys.getInt(1);
                    System.out.println("Generated Question ID: " + questionID);
                    // Gọi hàm thêm các lựa chọn
                    addChoices(conn, questionID);
                }
            }
        }
    }

    public void addChoices(Connection conn, int questionID) throws SQLException {
        String insertChoice = "INSERT INTO tbl_Choices (QuestionID, ChoiceText, IsCorrect) VALUES (?, ?, ?)";

        try (PreparedStatement choiceStmt = conn.prepareStatement(insertChoice)) {
            while (true) {
                // input choice
                String choiceText = Inputter.getString("Enter choice text: ", "Choice cannot be empty");
                choiceStmt.setString(2, choiceText);

                // pick questionID for choices
                choiceStmt.setInt(1, questionID);

                // is that the right choice?
                boolean isCorrect = Menu.isContinue("Is this the correct answer (yes/no)? ");
                choiceStmt.setBoolean(3, isCorrect);

                // insert into db
                choiceStmt.executeUpdate();
                System.out.println("Choice added successfully.");

                if (!Menu.isContinue("Do you want to add more choices for this question (yes/no)?")) {
                    break;
                }
            }
        }
    }
}
