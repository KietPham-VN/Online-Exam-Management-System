package repository;

import data.Exam;
import data.Question;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ExamRepository {

    // Insert methods
    public void insertExam(Connection conn, Exam examData) throws SQLException {
        String insertExam = "INSERT INTO tbl_Exams (ExamName, SubjectID, InstructorID, ExamDate, Duration, TotalMarks) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertExam, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, examData.getExamName());
            stmt.setInt(2, examData.getSubjectID());
            stmt.setInt(3, examData.getInstructorID());
            stmt.setDate(4, examData.getExamDate());
            stmt.setInt(5, examData.getDuration());
            stmt.setInt(6, examData.getTotalMarks());
            stmt.executeUpdate();
        }
    }

    public void insertQuestion(Connection conn, int examID, Question questionData) throws SQLException {
        String insertQuestion = "INSERT INTO tbl_Questions (QuestionText, QuestionType, Marks, ExamID, SubjectID) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertQuestion, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, questionData.getQuestionText());
            stmt.setString(2, questionData.getQuestionType());
            stmt.setInt(3, questionData.getMarks());
            stmt.setInt(4, questionData.getExamID());
            stmt.setInt(5, questionData.getSubjectID());
            stmt.executeUpdate();
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int questionID = generatedKeys.getInt(1);
                questionData.setQuestionID(questionID); // Set the ID in the Question object
            }
        }
    }

    public void insertChoice(Connection conn, int questionID, String choiceText, boolean isCorrect) throws SQLException {
        String insertChoiceSQL = "INSERT INTO tbl_Choices (QuestionID, ChoiceText, IsCorrect) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertChoiceSQL)) {
            stmt.setInt(1, questionID);
            stmt.setString(2, choiceText);
            stmt.setBoolean(3, isCorrect);
            stmt.executeUpdate();
        }
    }

    // Delete methods
    public void deleteExam(Connection conn, int examID) throws SQLException {
        String deleteChoices = "DELETE FROM tbl_Choices WHERE QuestionID IN (SELECT QuestionID FROM tbl_Questions WHERE ExamID = ?)";
        String deleteQuestions = "DELETE FROM tbl_Questions WHERE ExamID = ?";
        String deleteExam = "DELETE FROM tbl_Exams WHERE ExamID = ?";

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement deleteChoicesStmt = conn.prepareStatement(deleteChoices)) {
                deleteChoicesStmt.setInt(1, examID);
                deleteChoicesStmt.executeUpdate();
            }

            try (PreparedStatement deleteQuestionsStmt = conn.prepareStatement(deleteQuestions)) {
                deleteQuestionsStmt.setInt(1, examID);
                deleteQuestionsStmt.executeUpdate();
            }

            try (PreparedStatement deleteExamStmt = conn.prepareStatement(deleteExam)) {
                deleteExamStmt.setInt(1, examID);
                deleteExamStmt.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public void deleteQuestion(Connection conn, int questionID) throws SQLException {
        String deleteChoices = "DELETE FROM tbl_Choices WHERE QuestionID = ?";
        String deleteQuestion = "DELETE FROM tbl_Questions WHERE QuestionID = ?";

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement deleteChoicesStmt = conn.prepareStatement(deleteChoices)) {
                deleteChoicesStmt.setInt(1, questionID);
                deleteChoicesStmt.executeUpdate();
            }

            try (PreparedStatement deleteQuestionStmt = conn.prepareStatement(deleteQuestion)) {
                deleteQuestionStmt.setInt(1, questionID);
                deleteQuestionStmt.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public void deleteChoice(Connection conn, int choiceID) throws SQLException {
        String deleteChoice = "DELETE FROM tbl_Choices WHERE ChoiceID = ?";

        try (PreparedStatement deleteChoiceStmt = conn.prepareStatement(deleteChoice)) {
            deleteChoiceStmt.setInt(1, choiceID);
            deleteChoiceStmt.executeUpdate();
        }
    }

    public boolean checkExamExists(Connection conn, int examID) throws SQLException {
        String query = "SELECT COUNT(*) FROM tbl_Exams WHERE ExamID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, examID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Return true if count is greater than 0
            }
        }
        return false;
    }

    public boolean checkQuestionExists(Connection conn, int questionID) throws SQLException {
        String query = "SELECT COUNT(*) FROM tbl_Questions WHERE QuestionID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, questionID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Return true if count is greater than 0
            }
        }
        return false;
    }
    
    public boolean checkChoiceExists(Connection conn, int choiceID) throws SQLException {
        String query = "SELECT COUNT(*) FROM tbl_Questions WHERE QuestionID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, choiceID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Return true if count is greater than 0
            }
        }
        return false;
    }

    public boolean checkSubjectExists(Connection conn, int subjectID) throws SQLException {
        String query = "SELECT COUNT(*) FROM tbl_Subjects WHERE SubjectID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, subjectID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Return true if count is greater than 0
            }
        }
        return false;
    }
}
