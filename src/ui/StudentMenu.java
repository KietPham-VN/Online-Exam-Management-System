/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import controller.ExamExecuter;
import controller.UserController;
import data.User;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 * @author NGHIA
 */
public class StudentMenu {

    private User user;
    private UserController userController;
    private ExamExecuter examExecuter;
    private Scanner scanner = new Scanner(System.in);

    public StudentMenu(User user, UserController userController, ExamExecuter examExecuter) {
        this.user = user;
        this.userController = userController;
        this.examExecuter = examExecuter;
    }
    
    public void Print(Connection conn) throws SQLException{
        while (true) {
            Menu studentMenu = new Menu("Welcome back, " + user.getUsername() + " | Mode: Student");
            studentMenu.addNewOption("Do exams");
            studentMenu.addNewOption("View exam grades");
            studentMenu.addNewOption("Reset password");
            studentMenu.addNewOption("Logout");
            
            studentMenu.print();
            
            int choice = studentMenu.getChoice();
            switch (choice) {
                case 1: {
                    examExecuter.runExam(scanner, user.getUsername());
                    break;
                }
                case 2: {
                    userController.printStudentGrade(user.getUserID());
                    break;
                }
                case 3: {
                    userController.printResetPassword(user.getUserID());
                    break;
                }
                case 4: {
                    return;
                }
            }
        }
    }
}
