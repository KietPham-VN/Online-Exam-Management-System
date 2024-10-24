/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import data.ExamData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public ExamData examNameMenu(Connection conn) throws SQLException {
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

        return new ExamData(examName, subjectID);
    }
}
