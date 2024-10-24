/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import utils.Inputter;

/**
 *
 * @author Hoang Tran
 */
public class DeleteController {
    
    public void deleteExam(Connection conn) throws SQLException {
        
    }
    
    
    
    public void deleteQuestion(Connection conn) throws SQLException {
        //delete the choices in that question
        String deleteChoices = "DELETE FROM tbl_Choices WHERE QuestionID = ?";
        //delete the question
        String deleteQuestion = "DELETE FROM tbl_Questions WHERE QuestionID = ?";
        
        // questionID to delete
        int questionID = Inputter.getAnInteger("Enter the QuestionID to delete: ", "Invalid number");
        
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
        
        
        
        
    }
}
