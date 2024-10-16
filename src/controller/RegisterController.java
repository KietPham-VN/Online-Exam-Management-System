/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import ui.Menu;
import utils.Inputter;

/**
 *
 * @author NGHIA
 */
public class RegisterController {
    //Pass in the user model here
    public RegisterController(){}
    
    public void print(){
        while(true){
            String name = Inputter.getString("Enter name: ","Name must not be empty and have less or equal to 50 characters");
            String password = Inputter.getString("Enter password: ","Passowrd must not be empty and have more than 8 characters");
            String email = Inputter.getString("Enter email: ","Please enter a valid email address","^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
            String role = Inputter.getString("Enter role: ","Role must be Admin|Instructor|Student","^(Admin|Instructor|Student)$");
            //Add in the register function here. When the model is complete

            System.out.println("User registered:");
            //Print out the newly registered user
            if(!Menu.isContinue("Do you want to register more users (y/n)?")) return;
        }
    }
}
