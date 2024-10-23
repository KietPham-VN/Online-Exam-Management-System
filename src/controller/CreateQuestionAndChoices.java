package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import ui.Menu;
import utils.Inputter;

/**
 *
 * @author Hoang Tran
 */
public class CreateQuestionAndChoices {

    //Thêm câu hỏi
    public void addQuestion(Connection conn) throws SQLException {
        String insertQuesion = "INSERT INTO tbl_Questions (QuestionText, QuestionType, Marks, ExamID, SubjectID) VALUES (?, ?, ?, ?, ?)";
        String checkExamID = "SELECT COUNT(*) FROM tbl_Exams WHERE ExamID = ?";
        String checkSubjectID = "SELECT COUNT(*) FROM tbl_Subjects WHERE SubjectID = ?";
        try (PreparedStatement questionStmt = conn.prepareStatement(insertQuesion, PreparedStatement.RETURN_GENERATED_KEYS);
                PreparedStatement checkExamStmt = conn.prepareStatement(checkExamID);
                PreparedStatement checkSubjectStmt = conn.prepareStatement(checkSubjectID)) {
            int examID, subjectID;
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
            questionStmt.setString(2, questionType.toUpperCase());
            //Add marks for each ques
            double marks = Inputter.getADouble("Enter question marks: ", "Invalid number");
            questionStmt.setDouble(3, marks);

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
            questionStmt.setInt(4, examID);  // Set Exam ID here
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
                // Nhập lựa chọn
                String choiceText = Inputter.getString("Enter choice text: ", "Choice cannot be empty");
                choiceStmt.setString(2, choiceText);

                // Đặt QuestionID cho mỗi lựa chọn
                choiceStmt.setInt(1, questionID);

                // Xác định xem lựa chọn này có đúng không
                boolean isCorrect = Menu.isContinue("Is this the correct answer (yes/no)? ");
                choiceStmt.setBoolean(3, isCorrect);

                // Chèn lựa chọn vào cơ sở dữ liệu
                choiceStmt.executeUpdate();
                System.out.println("Choice added successfully.");

                // Hỏi người dùng có muốn tiếp tục thêm lựa chọn không
                if (!Menu.isContinue("Do you want to add more choices for this question (y/n)?")) {
                    break;
                }
            }
        }
    }
}
