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

/**
 *
 * @author Hoang Tran
 */
public class ValidationMarks {

    //current exam mark
    public int getTotalExamMarks(Connection conn, int examID) throws SQLException {
        String getTotalMarks = "SELECT TotalMarks FROM tbl_Exams WHERE ExamID = ?";
        try (PreparedStatement getTotalMarkStmt = conn.prepareStatement(getTotalMarks)) {
            getTotalMarkStmt.setInt(1, examID);
            ResultSet rs = getTotalMarkStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("TotalMarks");
            } else {
                throw new SQLException("Exam ID not found");
            }
        }
    }

    //take used mark
    public double getUsedMarkForExam(Connection conn, int examID) throws SQLException {
        String query = "SELECT SUM(Marks) AS totalUsedMark FROM tbl_Questions WHERE ExamID = ?";
        try (PreparedStatement getUsedMarkStmt = conn.prepareStatement(query)) {
            getUsedMarkStmt.setInt(1, examID);
            ResultSet rs = getUsedMarkStmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("totalUsedMark");
            } else {
                return 0.0;
            }
        }
    }

    public boolean validateQuestionMark(Connection conn, int examID, double questionMark) throws SQLException {
        double totalMark = getTotalExamMarks(conn, examID);
        double usedMark = getUsedMarkForExam(conn, examID);
        double remainingMark = totalMark - usedMark;

        // Kiểm tra xem số điểm nhập vào có vượt quá số điểm còn lại không
        if (questionMark > remainingMark) {
            System.out.println("The mark cannot be over: " + remainingMark);
            return false;
        } else {
            System.out.println("Valid marks. Available marks for this question: " + (remainingMark - questionMark));
            return true;
        }
    }
}
