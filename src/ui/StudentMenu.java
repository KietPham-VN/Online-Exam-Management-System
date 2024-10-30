/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import controller.UserController;
import data.User;

/**
 *
 * @author NGHIA
 */
public class StudentMenu {
        private User user;
    private UserController userController;
    public StudentMenu(User user,UserController userController){
        this.user=user;
        this.userController = userController;
    }
    
    public void Print(){
        while(true){
            Menu adminMenu = new Menu("Welcome back, "+user.getUsername()+" | Mode: Student");
            adminMenu.addNewOption("Do exams");
            adminMenu.addNewOption("View exam grades");
            adminMenu.addNewOption("Reset password");
            adminMenu.addNewOption("Logout");
            
            int choice=adminMenu.getChoice();
            switch(choice){
                case 1:{
                }
                case 2:{
                }
                case 3:{
                    userController.printResetPassword(user.getUserID());
                }
                case 4:{
                    return;
                }
            }
        }
    }
}
