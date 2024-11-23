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
public class AdminMenu {
    private User user;
    private UserController userController;
    public AdminMenu(User user,UserController userController){
        this.user=user;
        this.userController = userController;
    }
    
    public void Print(){
        while(true){
            Menu adminMenu = new Menu("Welcome back, "+user.getUsername()+" | Mode: Admin");
            adminMenu.addNewOption("Create new user");
            adminMenu.addNewOption("Reset password");
            adminMenu.addNewOption("Logout");
            
            adminMenu.print();
            int choice=adminMenu.getChoice();
            switch(choice){
                case 1:{
                    userController.printRegister();
                }
                case 2:{
                    userController.printResetPassword(user.getUserID());
                }
                case 3:{
                    return;
                }
            }
        }
    }
}
