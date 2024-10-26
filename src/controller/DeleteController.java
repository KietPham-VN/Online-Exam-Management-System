/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Hoang Tran
 */
public class DeleteController {

    private final InputHandler inputHandler;

    public DeleteController() {
        this.inputHandler = new InputHandler();
    }

    public void deleteExam(Connection conn) throws SQLException {
        String deleteChoices = "DELETE FROM tbl_Choices WHERE QuestionID IN (SELECT QuestionID FROM tbl_Questions WHERE ExamID = ?)";
        String deleteQuestions = "DELETE FROM tbl_Questions WHERE ExamID = ?";
        String deleteExam = "DELETE FROM tbl_Exams WHERE ExamID = ?";

        int examID = inputHandler.getExamIDToDelete(conn);

        try {
            conn.setAutoCommit(false);

            // delete related choices
            try (PreparedStatement deleteChoicesStmt = conn.prepareStatement(deleteChoices)) {
                deleteChoicesStmt.setInt(1, examID);
                int deletedChoices = deleteChoicesStmt.executeUpdate();
                System.out.println("Deleted " + deletedChoices + " related choices");
            }

            // delete related questions
            try (PreparedStatement deleteQuestionsStmt = conn.prepareStatement(deleteQuestions)) {
                deleteQuestionsStmt.setInt(1, examID);
                int deletedQuestions = deleteQuestionsStmt.executeUpdate();
                System.out.println("Deleted " + deletedQuestions + " related questions");
            }

            // finally delete the exam
            try (PreparedStatement deleteExamStmt = conn.prepareStatement(deleteExam)) {
                deleteExamStmt.setInt(1, examID);
                int deletedExam = deleteExamStmt.executeUpdate();

                if (deletedExam > 0) {
                    System.out.println("Exam with ID " + examID + " deleted successfully.");
                } else {
                    System.out.println("Exam with ID " + examID + " not found.");
                }
            }

            // Commit the transaction
            conn.commit();

        } catch (SQLException e) {
            conn.rollback();
            System.err.println("SQL Server error: " + e.getMessage());
            throw e;
        } finally {
            // Reset auto-commit to default
            conn.setAutoCommit(true);
        }
    }

    public void deleteQuestion(Connection conn) throws SQLException {
        //delete the choices in that question
        String deleteChoices = "DELETE FROM tbl_Choices WHERE QuestionID = ?";
        //delete the question
        String deleteQuestion = "DELETE FROM tbl_Questions WHERE QuestionID = ?";

        // questionID to delete
        int questionID = inputHandler.getQuestionIDToDelete();

        try {
            // cai nay la set cho câu lệnh delete không lưu thẳng vào dữ liệu
            conn.setAutoCommit(false);
            // delete related choices first
            try (PreparedStatement deleteChoicesStmt = conn.prepareStatement(deleteChoices)) {
                deleteChoicesStmt.setInt(1, questionID);
                int deletedChoices = deleteChoicesStmt.executeUpdate();
                System.out.println("Deleted " + deletedChoices + " related choices for question ID " + questionID);
            }

            // Now delete the question
            try (PreparedStatement deleteQuestionStmt = conn.prepareStatement(deleteQuestion)) {
                deleteQuestionStmt.setInt(1, questionID);
                int deletedQuestion = deleteQuestionStmt.executeUpdate();

                if (deletedQuestion > 0) {
                    System.out.println("Question with ID " + questionID + " deleted successfully.");
                } else {
                    System.out.println("Question with ID " + questionID + " not found.");
                }
            }
            // giờ mới lưu vào bằng tay
            conn.commit();

        } catch (SQLException e) {
            // cái này để quay lại lỡ mà lưu lỗi
            conn.rollback();
            System.err.println("SQL Server error: " + e.getMessage());
            throw e;
        } finally {
            // Reset auto-commit to default
            conn.setAutoCommit(true);
        }
    }

    public void deleteChoice(Connection conn) throws SQLException {
        String deleteChoice = "DELETE FROM tbl_Choices WHERE ChoiceID = ?";

        int choiceID = inputHandler.getChoiceIDToDelete(conn);

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement deleteChoiceStmt = conn.prepareStatement(deleteChoice)) {
                deleteChoiceStmt.setInt(1, choiceID);
                int deletedChoice = deleteChoiceStmt.executeUpdate();

                if (deletedChoice > 0) {
                    System.out.println("Choice with ID " + choiceID + " deleted successfully.");
                } else {
                    System.out.println("Choice with ID " + choiceID + " not found.");
                }
            }

            conn.commit();

        } catch (SQLException e) {
            conn.rollback();
            System.err.println("SQL Server error: " + e.getMessage());
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
}
