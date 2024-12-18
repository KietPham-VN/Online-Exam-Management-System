  /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import data.GradeReport;
import data.User;
import interfaces.repository.IUserRepository;
import java.util.ArrayList;
import java.util.Scanner;
import ui.Menu;
import utils.Inputter;

/**
 *
 * @author NGHIA
 */
public class UserController {
        //Pass in the user model here
    IUserRepository userRepository;
    public UserController(IUserRepository userRepository){
        this.userRepository=userRepository;
    }
    
    public User printLogin(){
        while(true){
            String name = Inputter.getString("Enter name: ","Name must not be empty and have less or equal to 50 characters","^.{1,50}$");
            String password = Inputter.getString("Enter password: ","Passowrd must not be empty and have more than 8 characters","^.{8,}$");

            User loginUser = userRepository.Login(name, password);
            
            if(loginUser!=null) return loginUser;
            else{
                System.out.println("Login failed. The username does not exist or the password is incorrect");
            }
        }
        
    }
    
    public void printRegister(){
        while(true){
            String name = Inputter.getString("Enter name: ","Name must not be empty and have less or equal to 50 characters","^.{1,50}$");
            
            while(userRepository.FindSingleUser(name)!=null){
                System.out.println("That name has already been registered.");
                name = Inputter.getString("Enter name: ","Name must not be empty and have less or equal to 50 characters","^.{1,50}$");
            }
            
            String password = Inputter.getString("Enter password: ","Password must not be empty and have more than 8 characters","^.{8,}$");
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
            
            
            //Print out the newly registered user/
            if(!Menu.isContinue("Do you want to register more users (y/n)?")) return;
        }
    }
    
    public User printRegisterWithNoUsers(){
        String name = Inputter.getString("Enter name: ","Name must not be empty and have less or equal to 50 characters","^.{1,50}$");
        String password = Inputter.getString("Enter password: ","Passowrd must not be empty and have more than 8 characters","^.{8,}$");
        String email = Inputter.getString("Enter email: ","Please enter a valid email address","^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");


        User newUser=userRepository.CreateUser(name, password, email, "Admin");

        if(newUser!=null){
            System.out.println("User registered:");
            System.out.println(newUser.toString());
        }
        else{
            System.out.println("Registered failed");
        }

       return newUser;
    }
    
    public void printResetPassword(int userID){
        while(true){
            String resetPassword = Inputter.getString("Enter the new password: ","Passowrd must not be empty and have more than 8 characters","^.{8,}$");

            if(userRepository.ResetPasword(userID, resetPassword)==1){
                System.out.println("Reset password successfully");
                return;
            }
            else{
                System.out.println("Password reset failed");
            }
        }
    }
    
    public void printStudentGrade(int userID){
        ArrayList<GradeReport> grades = userRepository.FindStudentGrade(userID);
        Scanner sc = new Scanner(System.in);
        
        if(grades.size()>0){
            for(int i=0;i<grades.size();i++){
                System.out.printf("Exam name: %s; Score: %d\n",grades.get(i).getExamName(),grades.get(i).getScore());
            }
        }
        else System.out.println("You have done 0 exams");

        System.out.print("Press Enter to continue");
        
        sc.nextLine();
    }
}
