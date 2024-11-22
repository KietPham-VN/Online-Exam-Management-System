package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import runtime.App;

public class ExamExecuter {
    private final Connection conn;

    // Constructor nhận Connection
    public ExamExecuter(Connection conn) {
        this.conn = conn;
    }

    public void runExam(Scanner scanner) throws SQLException {
        System.out.print("Enter ExamID: ");
        int examID = scanner.nextInt();

        if (!isValidExamID(examID)) {
            System.out.println("Invalid ExamID. Please check and try again.");
            return;
        }

        if (isExamCompleted(examID)) {
            System.out.println("You have already completed this exam.");
            return;
        }

        // Get exam duration
        String query = "SELECT Duration FROM tbl_Exams WHERE ExamID = ?";
        int duration = 0;
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, examID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                duration = rs.getInt("Duration");
            }
        }

        String countQuery = "SELECT COUNT(*) AS NumberOfQuestions FROM tbl_Questions WHERE ExamID = ?";
        int numberOfQuestions = 0;
        try (PreparedStatement pstmt = conn.prepareStatement(countQuery)) {
            pstmt.setInt(1, examID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                numberOfQuestions = rs.getInt("NumberOfQuestions");
            }
        }

        System.out.println("You have " + duration + " minutes to complete the exam.");
        System.out.println("Total number of questions: " + numberOfQuestions);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("\nTime is up! Submitting your exam.");
                try {
                    submitExam(examID);
                } catch (SQLException ex) {
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.exit(0);
            }
        }, duration * 60 * 1000);
    }

    private boolean isValidExamID(int examID) throws SQLException {
        String query = "SELECT COUNT(*) FROM tbl_Exams WHERE ExamID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, examID);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private boolean isExamCompleted(int examID) throws SQLException {
        String query = "SELECT IsCompleted FROM tbl_StudentExams WHERE ExamID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, examID);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getBoolean("IsCompleted");
        }
    }

    private void submitExam(int examID) throws SQLException {
        String updateSQL = "UPDATE tbl_StudentExams SET IsCompleted = 1, EndTime = GETDATE() WHERE ExamID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setInt(1, examID);
            pstmt.executeUpdate();
        }
        System.out.println("Your exam has been submitted.");
    }

    public void assignExamToStudent(Scanner scanner) {
        try {
            System.out.print("Enter ExamID to assign: ");
            int examID = scanner.nextInt();
            System.out.print("Enter StudentID to assign the exam: ");
            int studentID = scanner.nextInt();

            if (!isValidExamID(examID)) {
                System.out.println("Invalid ExamID. Please check and try again.");
                return;
            }

            if (!isValidStudentID(studentID)) {
                System.out.println("Invalid StudentID. Please check and try again.");
                return;
            }

            if (isExamAssignedToStudent(examID, studentID)) {
                System.out.println("Exam has already been assigned to this student.");
                return;
            }

            assignExam(examID, studentID);
            System.out.println("Exam assigned successfully to student with ID: " + studentID);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidStudentID(int studentID) throws SQLException {
        String query = "SELECT COUNT(*) FROM tbl_Users WHERE UserID = ? AND Role = 'Student'";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, studentID);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private boolean isExamAssignedToStudent(int examID, int studentID) throws SQLException {
        String query = "SELECT COUNT(*) FROM tbl_StudentExams WHERE ExamID = ? AND StudentID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, examID);
            pstmt.setInt(2, studentID);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private void assignExam(int examID, int studentID) throws SQLException {
        String insertSQL = "INSERT INTO tbl_StudentExams (ExamID, StudentID, IsCompleted) VALUES (?, ?, 0)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setInt(1, examID);
            pstmt.setInt(2, studentID);
            pstmt.executeUpdate();
        }
    }
}
