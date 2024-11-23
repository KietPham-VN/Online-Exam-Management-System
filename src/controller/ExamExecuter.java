package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ExamExecuter {

    private final Connection conn;

    public ExamExecuter(Connection conn) {
        this.conn = conn;
    }

    public void runExam(Scanner scanner, String username) throws SQLException {
        int studentID = getCurrentStudentID(username);
        System.out.print("Enter ExamID: ");
        int examID = scanner.nextInt();

        if (!isValidExamID(examID)) {
            System.out.println("Invalid ExamID. Please check and try again.");
            return;
        }

        if (isExamCompleted(examID, studentID)) {
            System.out.println("You have already completed this exam.");
            return;
        }

        List<Map<String, Object>> studentAnswers = new ArrayList<>();
        boolean examSubmitted = false;

        while (!examSubmitted) {
            displayAndCollectAnswers(scanner, examID, studentID, studentAnswers);

            System.out.println("\nDo you want to submit your answers? (yes/no)");
            String confirmation = scanner.next().trim().toLowerCase();

            if (confirmation.equals("yes")) {
                saveStudentAnswersToDatabase(studentAnswers, studentID, examID);
                submitExam(examID, studentID);
                System.out.println("Your answers have been submitted.");
                examSubmitted = true;
            } else if (confirmation.equals("no")) {
                System.out.println("\nReview your answers:");
                for (Map<String, Object> answer : studentAnswers) {
                    displayQuestionWithAnswer(scanner, answer);
                }
            } else {
                System.out.println("Invalid choice. Please enter 'yes' or 'no'.");
            }
        }
    }

    private void displayAndCollectAnswers(Scanner scanner, int examID, int studentID, List<Map<String, Object>> studentAnswers) throws SQLException {
        List<Map<String, Object>> questionsAndChoices = fetchQuestionsAndChoices(examID);

        char choiceLabel;
        int currentQuestionID = -1;

        for (Map<String, Object> questionData : questionsAndChoices) {
            int questionID = (int) questionData.get("QuestionID");
            String questionText = (String) questionData.get("QuestionText");
            String questionType = (String) questionData.get("QuestionType");

            if (questionID != currentQuestionID) {
                System.out.println("\nQuestion " + questionID + ": " + questionText);
                currentQuestionID = questionID;

                choiceLabel = 'a';

                if ("MCQ".equalsIgnoreCase(questionType)) {
                    System.out.println("Available choices:");
                    for (Map<String, Object> choice : questionsAndChoices) {
                        if ((int) choice.get("QuestionID") == questionID) {
                            System.out.println(" - " + choiceLabel + ". " + choice.get("ChoiceText"));
                            choice.put("ChoiceLabel", choiceLabel);
                            choiceLabel++;
                        }
                    }
                }

                getAnswerFromStudent(scanner, studentAnswers, questionID, questionType, questionsAndChoices);
            }
        }
    }

    private void getAnswerFromStudent(Scanner scanner, List<Map<String, Object>> studentAnswers, int questionID, String questionType, List<Map<String, Object>> questionsAndChoices) {
        Map<String, Object> currentAnswer = new HashMap<>();
        currentAnswer.put("QuestionID", questionID);
        currentAnswer.put("QuestionType", questionType);

        if ("MCQ".equalsIgnoreCase(questionType)) {
            System.out.print("Enter the choice (a, b, c, d...): ");
            char selectedChoiceLabel = scanner.next().trim().toLowerCase().charAt(0);

            // Chuyển từ 'a', 'b', 'c', 'd' sang 1, 2, 3, 4
            int selectedChoiceNumber = selectedChoiceLabel - 'a' + 1;

            // Tìm ChoiceID từ số thứ tự được chuyển đổi
            boolean choiceFound = questionsAndChoices.stream()
                .filter(choice -> (int) choice.get("QuestionID") == questionID)
                .skip(selectedChoiceNumber - 1) // Bỏ qua các lựa chọn trước đó
                .findFirst()
                .map(choice -> {
                    currentAnswer.put("SelectedChoiceID", choice.get("ChoiceID"));
                    return true;
                }).orElse(false);

            if (!choiceFound) {
                System.out.println("Invalid choice. Please try again.");
                getAnswerFromStudent(scanner, studentAnswers, questionID, questionType, questionsAndChoices);
            } else {
                // Kiểm tra tính hợp lệ của ChoiceID trong bảng tbl_Choices
                int choiceID = (int) currentAnswer.get("SelectedChoiceID");
                if (!isChoiceIDValid(choiceID)) {
                    System.out.println("Invalid ChoiceID. Please try again.");
                    getAnswerFromStudent(scanner, studentAnswers, questionID, questionType, questionsAndChoices);
                }
            }
        } else if ("ShortAnswer".equalsIgnoreCase(questionType)) {
            System.out.print("Enter your answer for this question: ");
            scanner.nextLine(); // Consume newline
            String shortAnswer = scanner.nextLine();
            currentAnswer.put("ShortAnswer", shortAnswer);
        }

        studentAnswers.add(currentAnswer);
    }

    // Kiểm tra tính hợp lệ của ChoiceID
    private boolean isChoiceIDValid(int choiceID) {
        String query = "SELECT COUNT(*) FROM tbl_Choices WHERE ChoiceID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, choiceID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            System.out.println("Error validating ChoiceID: " + ex.getMessage());
        }
        return false;
    }

    private void displayQuestionWithAnswer(Scanner scanner, Map<String, Object> answer) {
        int questionID = (int) answer.get("QuestionID");
        String questionType = (String) answer.get("QuestionType");

        System.out.println("\nQuestion " + questionID + ":");

        if ("MCQ".equalsIgnoreCase(questionType)) {
            System.out.println("Selected ChoiceID: " + answer.get("SelectedChoiceID"));
            System.out.print("Enter new choice (leave blank to keep current): ");
            scanner.nextLine();
            String input = scanner.nextLine();
            if (!input.isEmpty()) {
                answer.put("SelectedChoiceID", Integer.parseInt(input));
            }
        } else if ("ShortAnswer".equalsIgnoreCase(questionType)) {
            System.out.println("Short Answer: " + answer.get("ShortAnswer"));
            System.out.print("Enter new answer (leave blank to keep current): ");
            scanner.nextLine();
            String input = scanner.nextLine();
            if (!input.isEmpty()) {
                answer.put("ShortAnswer", input);
            }
        }
    }

    private List<Map<String, Object>> fetchQuestionsAndChoices(int examID) throws SQLException {
        List<Map<String, Object>> questionsAndChoices = new ArrayList<>();
        String fetchQuestionsQuery = "SELECT q.QuestionID, q.QuestionText, q.QuestionType, c.ChoiceID, c.ChoiceText "
            + "FROM tbl_Questions q "
            + "LEFT JOIN tbl_Choices c ON q.QuestionID = c.QuestionID "
            + "WHERE q.ExamID = ? "
            + "ORDER BY q.QuestionID, c.ChoiceID";

        try (PreparedStatement pstmt = conn.prepareStatement(fetchQuestionsQuery)) {
            pstmt.setInt(1, examID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> questionData = new HashMap<>();
                questionData.put("QuestionID", rs.getInt("QuestionID"));
                questionData.put("QuestionText", rs.getString("QuestionText"));
                questionData.put("QuestionType", rs.getString("QuestionType"));
                questionData.put("ChoiceID", rs.getInt("ChoiceID"));
                questionData.put("ChoiceText", rs.getString("ChoiceText"));
                questionsAndChoices.add(questionData);
            }
        }
        return questionsAndChoices;
    }

    private void saveStudentAnswersToDatabase(List<Map<String, Object>> answers, int studentID, int examID) throws SQLException {
        int studentExamID = getStudentExamID(studentID, examID);

        String insertAnswerQuery = "INSERT INTO tbl_StudentAnswers (StudentExamID, SelectedAnswerID, ShortAnswer) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertAnswerQuery)) {
            for (Map<String, Object> answer : answers) {
                pstmt.setInt(1, studentExamID);

                if (answer.get("SelectedChoiceID") != null) {
                    pstmt.setInt(2, (int) answer.get("SelectedChoiceID"));
                } else {
                    pstmt.setNull(2, java.sql.Types.INTEGER);
                }

                if (answer.get("ShortAnswer") != null) {
                    pstmt.setString(3, (String) answer.get("ShortAnswer"));
                } else {
                    pstmt.setNull(3, java.sql.Types.VARCHAR);
                }

                pstmt.executeUpdate();
            }
        }
    }

    private int getStudentExamID(int studentID, int examID) throws SQLException {
        String query = "SELECT StudentExamID FROM tbl_StudentExams WHERE StudentID = ? AND ExamID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, studentID);
            pstmt.setInt(2, examID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("StudentExamID");
            }
        }
        throw new SQLException("StudentExamID not found for StudentID: " + studentID + ", ExamID: " + examID);
    }

    private int getCurrentStudentID(String username) throws SQLException {
        String query = "SELECT UserID FROM tbl_Users WHERE Username = ? AND Role = 'Student'";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("UserID");
            }
        }
        throw new SQLException("StudentID not found for username: " + username);
    }

    private boolean isValidExamID(int examID) throws SQLException {
        String query = "SELECT COUNT(*) FROM tbl_Exams WHERE ExamID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, examID);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private boolean isExamCompleted(int examID, int studentID) throws SQLException {
        String query = "SELECT IsCompleted FROM tbl_StudentExams WHERE ExamID = ? AND StudentID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, examID);
            pstmt.setInt(2, studentID);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getBoolean("IsCompleted");
        }
    }

    private void submitExam(int examID, int studentID) throws SQLException {
        String updateSQL = "UPDATE tbl_StudentExams SET IsCompleted = 1, EndTime = GETDATE() WHERE ExamID = ? AND StudentID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setInt(1, examID);
            pstmt.setInt(2, studentID);
            pstmt.executeUpdate();
        }
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
