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

    private final User user;
    private final UserController userController;
    private final ExamExecuter examExecuter;
    private final ExamSubMenu examSubMenu;
    private final Scanner scanner = new Scanner(System.in);

    public InstructorMenu(User user, UserController userController, ExamExecuter examExecuter, ExamSubMenu examSubMenu) {
        this.user = user;
        this.userController = userController;
        this.examExecuter = examExecuter;
        this.examSubMenu = examSubMenu;
    }

    public void Print(Connection conn) {
        while (true) {
            Menu instructorMenu = new Menu("Welcome back, " + user.getUsername() + " | Mode: Instructor");
            instructorMenu.addNewOption("Manage subjects and exams");
            instructorMenu.addNewOption("Assign exams");
            instructorMenu.addNewOption("Reset password");
            instructorMenu.addNewOption("Logout");

            instructorMenu.print();
            int choice = instructorMenu.getChoice();
            

            switch (choice) {
                case 1: {
                    examSubMenu.manageExams();
                    break;
                }
                case 2: {
                    examExecuter.assignExamToStudent( scanner);
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
