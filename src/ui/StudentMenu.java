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
    private Scanner scanner = new Scanner(System.in);

    public StudentMenu(User user, UserController userController) {
        this.user = user;
        this.userController = userController;
    }
    
    public void Print(Connection conn) throws SQLException{
        while (true) {
            Menu adminMenu = new Menu("Welcome back, " + user.getUsername() + " | Mode: Student");
            adminMenu.addNewOption("1. Do exams");
            adminMenu.addNewOption("2. View exam grades");
            adminMenu.addNewOption("3. Reset password");
            adminMenu.addNewOption("4. Logout");
            
            int choice = adminMenu.getChoice();
            switch (choice) {
                case 1: {
                    ExamExecuter.runExam(conn, scanner);
                }
                case 2: {
                }
                case 3: {
                    userController.printResetPassword(user.getUserID());
                }
                case 4: {
                    return;
                }
            }
        }
    }
}
