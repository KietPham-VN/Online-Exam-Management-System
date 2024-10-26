package runtime;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    private static final String SERVER = "urjellyfish.mssql.somee.com";
    private static final String USER = "urjellyfish_SQLLogin_1";
    private static final String PASSWORD = "5medi3d4w6";
    private static final String DB = "urjellyfish";
    private static final int PORT = 1433;

    public static void main(String[] args) {
        try (Connection conn = createConnection()) {
            System.out.println("Connected to the database.");
            Scanner scanner = new Scanner(System.in);

            // Step 4: Run the exam
            runExam(conn, scanner);
        } catch (SQLServerException e) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, "SQL Server Exception: ", e);
        } catch (SQLException e) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, "SQL Exception: ", e);
        }
    }

    private static Connection createConnection() throws SQLServerException {
        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setUser(USER);
        ds.setPassword(PASSWORD);
        ds.setDatabaseName(DB);
        ds.setServerName(SERVER);
        ds.setPortNumber(PORT);
        ds.setEncrypt(false);
        ds.setTrustServerCertificate(true);
        return ds.getConnection();
    }

    private static void runExam(Connection conn, Scanner scanner) throws SQLException {
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
                if (questions.stream().noneMatch(q -> q.getId() == questionId)) {
                    questions.add(new Question(questionId, questionText, marks));
                }

                // Add choices to the last question in the list
                questions.get(questions.size() - 1).addChoice(choiceText, isCorrect);
            }
        }

        // Display questions and collect answers
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            System.out.printf("%d. %s\nChoices:\n", i + 1, question.getText());
            for (int j = 0; j < question.getChoices().size(); j++) {
                System.out.printf("%c. %s\t", 'a' + j, question.getChoices().get(j).getText());
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
                insertStudentAnswer(conn, studentExamID, questions.get(i).getId(), selectedAnswers.get(i));
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

    static class Question {

        private final int id;
        private final String text;
        private final int marks;
        private final List<Choice> choices = new ArrayList<>();

        public Question(int id, String text, int marks) {
            this.id = id;
            this.text = text;
            this.marks = marks;
        }

        public int getId() {
            return id;
        }

        public String getText() {
            return text;
        }

        public int getMarks() {
            return marks;
        }

        public void addChoice(String choiceText, boolean isCorrect) {
            choices.add(new Choice(choiceText, isCorrect));
        }

        public List<Choice> getChoices() {
            return choices;
        }

        public boolean isCorrect(String selectedChoice) {
            int index = selectedChoice.charAt(0) - 'a';
            return index >= 0 && index < choices.size() && choices.get(index).isCorrect();
        }
    }

    static class Choice {

        private final String text;
        private final boolean isCorrect;

        public Choice(String text, boolean isCorrect) {
            this.text = text;
            this.isCorrect = isCorrect;
        }

        public String getText() {
            return text;
        }

        public boolean isCorrect() {
            return isCorrect;
        }
    }
}
