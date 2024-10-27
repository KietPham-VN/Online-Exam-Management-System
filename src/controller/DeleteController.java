/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.sql.Connection;
import java.sql.SQLException;
import repository.ExamRepository;
import utils.Inputter;

/**
 *
 * @author Hoang Tran
 */
public class DeleteController {

    private final ExamRepository examRepository;

    public DeleteController() {
        this.examRepository = new ExamRepository();
    }

    public void deleteExam(Connection conn) throws SQLException {
        int examID = Inputter.getAnInteger("Enter examID you want to delete: ", "ExamID cannot be empty");
        if (!examRepository.checkExamExists(conn, examID)) {
            System.out.println("Exam ID not found");
        } else {
            try {
                examRepository.deleteExam(conn, examID);
                System.out.println("Exam with ID " + examID + " and all related data deleted successfully.");
            } catch (SQLException e) {
                System.err.println("Error deleting exam: " + e.getMessage());
                throw e;
            }
        }
    }

    public void deleteQuestion(Connection conn) throws SQLException {
        int questionID = Inputter.getAnInteger("Enter questionID you want to delete: ", "QuestionID cannot be empty");
        if (!examRepository.checkQuestionExists(conn, questionID)) {
            System.out.println("Question ID not found");
        } else {
            try {
                examRepository.deleteQuestion(conn, questionID);
                System.out.println("Question with ID " + questionID + " and all related choices deleted successfully.");
            } catch (SQLException e) {
                System.err.println("Error deleting question: " + e.getMessage());
                throw e;
            }
        }
    }

    public void deleteChoice(Connection conn) throws SQLException {
        int choiceID = Inputter.getAnInteger("Enter choiceID you want to delete: ", "ChoiceID cannot be empty");
        if (!examRepository.checkChoiceExists(conn, choiceID)) {
            System.out.println("Choice ID not found");
        } else {
            try {
                examRepository.deleteChoice(conn, choiceID);
                System.out.println("Choice with ID " + choiceID + " deleted successfully.");
            } catch (SQLException e) {
                System.err.println("Error deleting choice: " + e.getMessage());
                throw e;
            }
        }
    }
}