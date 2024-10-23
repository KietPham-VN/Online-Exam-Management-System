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
import java.time.LocalDate;
import java.util.Date;
import ui.Menu;
import utils.Inputter;

/**
 *
 * @author NGHIA
 */
public class CreateExamController {

    //Pass in the exam model here
    public CreateExamController() {
    }

    public void addExam(Connection conn) throws SQLException {
        String insertExam = "INSERT INTO tbl_Exams (ExamName, SubjectID, InstructorID, ExamDate, Duration, TotalMarks) VALUES (?, ?, ?, ?, ?, ?)";
        String checkSubject = "SELECT COUNT(*) FROM tbl_Subjects WHERE SubjectID = ?"; // check if the subject ID exists
        try (PreparedStatement examStmt = conn.prepareStatement(insertExam, PreparedStatement.RETURN_GENERATED_KEYS);
                PreparedStatement checkSubjectStmt = conn.prepareStatement(checkSubject)) {
            while (true) {
                String examName = Inputter.getString("Enter the exam name: ", "Exam name must not be empty");
                examStmt.setString(1, examName);
                int subjectID;
                while (true) {
                    subjectID = Inputter.getAnInteger("Enter the subjectID of the exam: ", "SubjectID must not be an integer");
                    checkSubjectStmt.setInt(1, subjectID);
                    try (ResultSet rs = checkSubjectStmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) { // If subject exists
                            break;
                        } else {
                            System.out.println("Invalid SubjectID. Please enter a valid SubjectID.");
                        }
                    }
                }
                examStmt.setInt(2, subjectID);

                int instructorID = Inputter.getAnInteger("Enter the instructorID of the exam:", "InstructorID must be an integer");
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
}
