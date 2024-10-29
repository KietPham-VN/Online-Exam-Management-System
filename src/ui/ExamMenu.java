/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import data.Exam;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import utils.Inputter;

/**
 *
 * @author Hoang Tran
 */
public class ExamMenu {

    public String getSubjectName(Connection conn, int subjectID) throws SQLException {
        String getSubject = "SELECT SubjectName FROM tbl_Subjects WHERE SubjectID = ?";
        try (PreparedStatement getSubjectStmt = conn.prepareStatement(getSubject)) {
            getSubjectStmt.setInt(1, subjectID);
            ResultSet rs = getSubjectStmt.executeQuery();
            if (rs.next()) {
                return rs.getString("SubjectName");
            } else {
                throw new SQLException("Subject not valid");
            }
        }
    }

    public int subjectNameMenu() {
        System.out.println("1. Mathematics");
        System.out.println("2. Computer Science");
        System.out.println("3. Physics");
        System.out.println("4. Chemistry");
        System.out.println("5. Biology");
        System.out.println("6. English Literature");
        System.out.println("7. History");
        System.out.println("8. Geography");
        System.out.println("9. Economics");
        System.out.println("10. Art");

        int subjectID = Inputter.getAnInteger("Enter your choice: ", "Invalid input", 1, 10);
        return subjectID;
    }

    public Exam examNameMenu(Connection conn) throws SQLException {
        System.out.println("1. Midterm Exam");
        System.out.println("2. Final Exam");
        System.out.println("3. Other");

        int choice = Inputter.getAnInteger("Enter your choice: ", "Invalid input", 1, 3);
        String examType;
        if (choice == 1) {
            examType = "Midterm Exam";
        } else if (choice == 2) {
            examType = "Final Exam";
        } else {
            examType = Inputter.getString("Enter exam name: ", "Exam name cannot be empty");
        }

        int subjectID = subjectNameMenu();
        String subjectName = getSubjectName(conn, subjectID);
        String examName = examType + " - " + subjectName;

        // input for other Exam fields
        int instructorID = Inputter.getAnInteger("Enter Instructor ID: ", "Invalid input", 1, Integer.MAX_VALUE);
        int duration = Inputter.getAnInteger("Enter exam duration in minutes: ", "Invalid input", 1, 500);
        int totalMarks = Inputter.getAnInteger("Enter total marks: ", "Invalid input", 1, Integer.MAX_VALUE);

        Date examDate = inputExamDate();  // Replace with your date input method

        return new Exam(0, examName, subjectID, instructorID, examDate, duration, totalMarks);
    }

    public static Date inputExamDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false); // Strict parsing
        while (true) {
            String dateInput = Inputter.getString("Enter exam date (dd-MM-yyyy): ", "Date cannot be empty");
            try {
                java.util.Date parsedDate = dateFormat.parse(dateInput);
                if(parsedDate.before(new java.util.Date())) System.out.println("Exam date cannot be earlier than today.");
                else return new Date(parsedDate.getTime()); // Convert to java.sql.Date
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please try again");
            }
        }
    }
}