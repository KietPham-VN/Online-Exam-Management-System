/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import data.Choice;
import data.Question;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import runtime.App;

/**
 *
 * @author anhkietz
 */
public class ExamExecuter {
    public static void runExam(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter ExamID: ");
        int examID = scanner.nextInt();

        if (!isValidExamID(conn, examID)) {
            System.out.println("Invalid ExamID. Please check and try again.");
            return;
        }

        if (isExamCompleted(conn, examID)) {
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
                    submitExam(conn, examID);
                } catch (SQLException ex) {
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.exit(0);
            }
        }, duration * 60 * 1000);

        String questionQuery = "SELECT q.QuestionID, q.QuestionText, c.ChoiceID, c.ChoiceText, c.IsCorrect, q.Marks "
            + "FROM tbl_Questions q "
            + "JOIN tbl_Choices c ON q.QuestionID = c.QuestionID "
            + "WHERE q.ExamID = ?";

        List<Question> questions = new ArrayList<>();
        List<String> selectedAnswers = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(questionQuery)) {
            pstmt.setInt(1, examID);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int questionId = rs.getInt("QuestionID");
                String questionText = rs.getString("QuestionText");
                String choiceText = rs.getString("ChoiceText");
                boolean isCorrect = rs.getBoolean("IsCorrect");
                int marks = rs.getInt("Marks");

                // Only add the question once
                Question question = questions.stream()
                    .filter(q -> q.getQuestionID() == questionId)
                    .findFirst()
                    .orElse(null);

                if (question == null) {
                    question = new Question(questionId, questionText, "multiple-choice", marks, examID, 0); // Assuming subjectID is not used
                    questions.add(question);
                }

                // Add choices to the question
                question.addChoice(new Choice(rs.getInt("ChoiceID"), questionId, choiceText, isCorrect));
            }
        }

        // Display questions and collect answers
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            System.out.printf("%d. %s\nChoices:\n", i + 1, question.getQuestionText());
            for (int j = 0; j < question.getChoices().size(); j++) {
                System.out.printf("%c. %s\t", 'a' + j, question.getChoices().get(j).getChoiceText());
            }
            System.out.println();

            String selectedChoice;
            do {
                System.out.print("Select your answer (a, b, c, d): ");
                selectedChoice = scanner.next();
                if (!selectedChoice.matches("[a-" + (char) ('a' + question.getChoices().size() - 1) + "]")) {
                    System.out.println("Invalid choice. Please select a valid option (a, b, c, d).");
                }
            } while (!selectedChoice.matches("[a-" + (char) ('a' + question.getChoices().size() - 1) + "]"));
            selectedAnswers.add(selectedChoice);
        }

        // Calculate score based on selected answers
        int totalScore = 0;
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            if (question.isCorrect(selectedAnswers.get(i))) {
                totalScore += question.getMarks();
            }
        }

        // Ask if they want to submit the exam
        System.out.print("Do you want to submit your exam? (yes/no): ");
        String submitResponse = scanner.next();
        if (submitResponse.equalsIgnoreCase("yes")) {
            int studentExamID = insertStudentExam(conn, examID);

            for (int i = 0; i < questions.size(); i++) {
                insertStudentAnswer(conn, studentExamID, questions.get(i).getQuestionID(), selectedAnswers.get(i));
            }

            insertGrade(conn, studentExamID, totalScore);
            System.out.println("Your score is: " + totalScore);
            submitExam(conn, studentExamID); // Ensure the exam is submitted
        } else {
            System.out.println("Your exam has been cancelled.");
        }
    }

    private static boolean isValidExamID(Connection conn, int examID) throws SQLException {
        String query = "SELECT COUNT(*) FROM tbl_Exams WHERE ExamID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, examID);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private static boolean isExamCompleted(Connection conn, int examID) throws SQLException {
        String query = "SELECT IsCompleted FROM tbl_StudentExams WHERE ExamID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, examID);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getBoolean("IsCompleted");
        }
    }

    private static int insertStudentExam(Connection conn, int examID) throws SQLException {
        String insertSQL = "INSERT INTO tbl_StudentExams (ExamID, StudentID, IsCompleted) VALUES (?, ?, 0);";
        int studentID = 1; // Assume you have the student ID from context or session
        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, examID);
            pstmt.setInt(2, studentID);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    private static void insertStudentAnswer(Connection conn, int studentExamID, int questionID, String selectedChoice) throws SQLException {
        int selectedAnswerID = getChoiceID(conn, questionID, selectedChoice);
        String insertSQL = "INSERT INTO tbl_StudentAnswers (StudentExamID, SelectedAnswerID) VALUES (?, ?);";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setInt(1, studentExamID);
            pstmt.setInt(2, selectedAnswerID);
            pstmt.executeUpdate();
        }
    }

    private static int getChoiceID(Connection conn, int questionID, String selectedChoice) throws SQLException {
        int choiceIndex = selectedChoice.charAt(0) - 'a';
        String query = "SELECT ChoiceID FROM tbl_Choices WHERE QuestionID = ? ORDER BY ChoiceID OFFSET ? ROWS FETCH NEXT 1 ROWS ONLY;";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, questionID);
            pstmt.setInt(2, choiceIndex);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("ChoiceID");
            }
        }
        return -1;
    }

    private static void insertGrade(Connection conn, int studentExamID, int totalScore) throws SQLException {
        String insertSQL = "INSERT INTO tbl_Grades (StudentExamID, TotalScore) VALUES (?, ?);";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setInt(1, studentExamID);
            pstmt.setInt(2, totalScore);
            pstmt.executeUpdate();
        }
    }

    private static void submitExam(Connection conn, int studentExamID) throws SQLException {
        String updateSQL = "UPDATE tbl_StudentExams SET IsCompleted = 1, EndTime = GETDATE() WHERE StudentExamID = ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setInt(1, studentExamID);
            pstmt.executeUpdate();
        }
        System.out.println("Your exam has been submitted.");
    }
    
    public static void assignExamToStudent(Connection conn, Scanner scanner) {
        try {
            System.out.print("Enter ExamID to assign: ");
            int examID = scanner.nextInt();
            System.out.print("Enter StudentID to assign the exam: ");
            int studentID = scanner.nextInt();

            if (!isValidExamID(conn, examID)) {
                System.out.println("Invalid ExamID. Please check and try again.");
                return;
            }

            if (!isValidStudentID(conn, studentID)) {
                System.out.println("Invalid StudentID. Please check and try again.");
                return;
            }

            if (isExamAssignedToStudent(conn, examID, studentID)) {
                System.out.println("Exam has already been assigned to this student.");
                return;
            }

            assignExam(conn, examID, studentID);
            System.out.println("Exam assigned successfully to student with ID: " + studentID);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra StudentID có hợp lệ không
    private static boolean isValidStudentID(Connection conn, int studentID) throws SQLException {
        String query = "SELECT COUNT(*) FROM tbl_Users WHERE UserID = ? AND Role = 'Student'";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, studentID);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    // Kiểm tra xem kỳ thi đã được giao cho học sinh chưa
    private static boolean isExamAssignedToStudent(Connection conn, int examID, int studentID) throws SQLException {
        String query = "SELECT COUNT(*) FROM tbl_StudentExams WHERE ExamID = ? AND StudentID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, examID);
            pstmt.setInt(2, studentID);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    // Giao kỳ thi cho học sinh (chèn vào bảng tbl_StudentExams)
    private static void assignExam(Connection conn, int examID, int studentID) throws SQLException {
        String insertSQL = "INSERT INTO tbl_StudentExams (ExamID, StudentID, IsCompleted) VALUES (?, ?, 0)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setInt(1, examID);
            pstmt.setInt(2, studentID);
            pstmt.executeUpdate();
        }
    }
}
