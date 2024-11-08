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
import java.util.Scanner;

/**
 *
 * @author NGHIA
 */
public class InstructorMenu {

    private User user;
    private UserController userController;
    private Scanner scanner = new Scanner(System.in);

    public InstructorMenu(User user, UserController userController) {
        this.user = user;
        this.userController = userController;
    }

    SubMenu subMenu = new SubMenu();
    
    public void Print(Connection conn) {
        while (true) {
            Menu adminMenu = new Menu("Welcome back, " + user.getUsername() + " | Mode: Instructor");
            adminMenu.addNewOption("1. Manage subjects and exams");
            adminMenu.addNewOption("2. Assign exams");
            adminMenu.addNewOption("3. Reset password");
            adminMenu.addNewOption("4. Logout");

            int choice = adminMenu.getChoice();
            
            switch (choice) {
                case 1: {
                    subMenu.manageExams();
                    break;
                }
                case 2: {
                    ExamExecuter.assignExamToStudent(conn, scanner);
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
