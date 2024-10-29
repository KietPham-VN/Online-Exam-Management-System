package repository;

import data.Choice;
import data.Exam;
import data.Question;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
    
    public Exam findExam(Connection conn, int examID) throws SQLException {
        String query = "SELECT * FROM tbl_Exams WHERE ExamID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, examID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Assuming Exam has a constructor to set its fields from the ResultSet
                return new Exam(
                    rs.getInt("ExamID"),
                    rs.getString("ExamName"),
                    rs.getInt("SubjectID"),
                    rs.getInt("InstructorID"),
                    rs.getDate("ExamDate"),
                    rs.getInt("Duration"),
                    rs.getInt("TotalMarks")
                );
            }
        }
        return null; // Return null if exam not found
    }
    
    public ArrayList<Question> findQuestionsInExam(Connection conn, int examID) throws SQLException {
       String query = "SELECT * FROM tbl_Questions WHERE ExamID = ?";
       ArrayList<Question> questions = new ArrayList<>();

       try (PreparedStatement stmt = conn.prepareStatement(query)) {
           stmt.setInt(1, examID);
           ResultSet rs = stmt.executeQuery();

           while (rs.next()) {
               // Create a new Question object for each row in the ResultSet
               Question question = new Question(
                   rs.getInt("QuestionID"),
                   rs.getString("QuestionText"),
                   rs.getString("QuestionType"),
                   rs.getInt("Marks"),
                   rs.getInt("ExamID"),
                   rs.getInt("SubjectID")
               );
               questions.add(question); // Add the Question object to the list
           }
       }

       return questions; // Return the list of questions for the exam
   }

    public Question findQuestion(Connection conn, int questionID) throws SQLException {
        String query = "SELECT * FROM tbl_Questions WHERE QuestionID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, questionID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Assuming Question has a constructor to set its fields from the ResultSet
                return new Question(
                    rs.getInt("QuestionID"),
                    rs.getString("QuestionText"),
                    rs.getString("QuestionType"),
                    rs.getInt("Marks"),
                    rs.getInt("ExamID"),
                    rs.getInt("SubjectID")
                );
            }
        }
        return null; // Return null if question not found
    }

    public Choice findChoice(Connection conn, int answerID) throws SQLException {
        String query = "SELECT * FROM tbl_Choices WHERE ChoiceID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, answerID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Assuming Answer has a constructor to set its fields from the ResultSet
                return new Choice(
                    rs.getInt("ChoiceID"),
                    rs.getInt("QuestionID"),
                    rs.getString("ChoiceText"),
                    rs.getBoolean("IsCorrect")
                );
            }
        }
        return null; // Return null if answer not found
    }

    public boolean checkExamExists(Connection conn, int examID) throws SQLException {
       return findExam(conn,examID)!=null;
    }

    public boolean checkQuestionExists(Connection conn, int questionID) throws SQLException {
        return findQuestion(conn,questionID)!=null;
    }
    
    public boolean checkChoiceExists(Connection conn, int choiceID) throws SQLException {
        return findChoice(conn,choiceID)!=null;
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
    
     // Update an exam
    public void updateExam(Connection conn, int examID, Exam updatedExam) throws SQLException {
        String updateExamSQL = "UPDATE tbl_Exams SET ExamName = ?, SubjectID = ?, InstructorID = ?, ExamDate = ?, Duration = ?, TotalMarks = ? WHERE ExamID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateExamSQL)) {
            stmt.setString(1, updatedExam.getExamName());
            stmt.setInt(2, updatedExam.getSubjectID());
            stmt.setInt(3, updatedExam.getInstructorID());
            stmt.setDate(4, updatedExam.getExamDate());
            stmt.setInt(5, updatedExam.getDuration());
            stmt.setInt(6, updatedExam.getTotalMarks());
            stmt.setInt(7, examID);
            stmt.executeUpdate();
        }
    }

    // Update a question
    public void updateQuestion(Connection conn, int questionID, Question updatedQuestion) throws SQLException {
        String updateQuestionSQL = "UPDATE tbl_Questions SET QuestionText = ?, QuestionType = ?, Marks = ?, ExamID = ?, SubjectID = ? WHERE QuestionID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateQuestionSQL)) {
            stmt.setString(1, updatedQuestion.getQuestionText());
            stmt.setString(2, updatedQuestion.getQuestionType());
            stmt.setInt(3, updatedQuestion.getMarks());
            stmt.setInt(4, updatedQuestion.getExamID());
            stmt.setInt(5, updatedQuestion.getSubjectID());
            stmt.setInt(6, questionID);
            stmt.executeUpdate();
        }
    }

    // Update a choice
    public void updateChoice(Connection conn, int choiceID, String choiceText, boolean isCorrect) throws SQLException {
        String updateChoiceSQL = "UPDATE tbl_Choices SET ChoiceText = ?, IsCorrect = ? WHERE ChoiceID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateChoiceSQL)) {
            stmt.setString(1, choiceText);
            stmt.setBoolean(2, isCorrect);
            stmt.setInt(3, choiceID);
            stmt.executeUpdate();
        }
    }
}
