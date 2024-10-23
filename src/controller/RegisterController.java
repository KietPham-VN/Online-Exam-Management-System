/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import data.User;
import interfaces.repository.IUserRepository;
import ui.Menu;
import utils.Inputter;

/**
 *
 * @author NGHIA
 */
public class RegisterController {
    //Pass in the user model here
    IUserRepository userRepository;
    public RegisterController(IUserRepository userRepository){
        this.userRepository=userRepository;
    }
    
    public void print(){
        while(true){
            String name = Inputter.getString("Enter name: ","Name must not be empty and have less or equal to 50 characters","^.{1,50}$");
            String password = Inputter.getString("Enter password: ","Passowrd must not be empty and have more than 8 characters","^.{8}$");
            String email = Inputter.getString("Enter email: ","Please enter a valid email address","^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
            String role = Inputter.getString("Enter role: ","Role must be Admin|Instructor|Student","^(Admin|Instructor|Student)$");

            
            User newUser=userRepository.CreateUser(name, password, email, role);
            
            if(newUser!=null){
                System.out.println("User registered:");
                System.out.println(newUser.toString());
            }
            else{
                System.out.println("Registered failed");
            }
            
            
            //Print out the newly registered user
            if(!Menu.isContinue("Do you want to register more users (y/n)?")) return;
        }
    }
}
