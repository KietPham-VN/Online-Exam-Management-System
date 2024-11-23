/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import controller.CreateExamController;
import controller.DeleteExamController;
import controller.ExamExecuter;
import controller.UpdateExamController;
import controller.UserController;
import data.User;
import db.database;
import interfaces.repository.IUserRepository;
import java.sql.Connection;
import java.util.ArrayList;
import repository.UserRepository;

/**
 *
 * @author NGHIA
 */
public class StartupMenu {

    public static void Print() {
        String server = "Online-Exam-Management-System.mssql.somee.com";
        String user = "urjellyfish_SQLLogin_1";
        String password = "5medi3d4w6";
        String db = "Online-Exam-Management-System";
        int port = 1433;

        database ds = new database(server, user, password, db, port);

        try {
            Connection conn = ds.connect();
            IUserRepository userRepo = new UserRepository(conn);
            UserController userController = new UserController(userRepo);
            ExamExecuter examExecuter = new ExamExecuter(conn);
            CreateExamController createExamController = new CreateExamController(conn);
            UpdateExamController updateExamController = new UpdateExamController(conn);
            DeleteExamController deleteExamController = new DeleteExamController(conn);
            ExamSubMenu examSubMenu = new ExamSubMenu(createExamController,updateExamController,deleteExamController);

            User loginUser;
            //Check if there a user in the system
            ArrayList<User> users = userRepo.FindUsers("");

            System.out.println("*****WELCOME TO ONLINE EXAM MANAGEMENT*****");
            if (users.isEmpty()) {
                System.out.println("Cannot find a single user. Please register a new user as an admin.");
                loginUser = userController.printRegisterWithNoUsers();
            } else {
                System.out.println("Login in");
                loginUser = userController.printLogin();
            }

            //Pass loginuser to more controller
            switch (loginUser.getRole()) {
                case "admin": {
                    AdminMenu adminMenu = new AdminMenu(loginUser, userController);
                    adminMenu.Print();
                }
                case "instructor": {
                    InstructorMenu instructorMenu = new InstructorMenu(loginUser, userController, examExecuter, examSubMenu);
                    instructorMenu.Print(conn);
                }
                case "student": {
                    StudentMenu studentMenu = new StudentMenu(loginUser, userController,examExecuter);
                    studentMenu.Print(conn);
                }
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
