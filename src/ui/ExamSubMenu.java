/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import controller.CreateExamController;
import controller.DeleteExamController;
import controller.UpdateExamController;
import java.sql.Connection;
import java.sql.SQLException;
import utils.Inputter;

/**
 *
 * @author Hoang Tran
 */
public class ExamSubMenu {

    private final CreateExamController createExam;
    private final UpdateExamController updateExam;
    private final DeleteExamController deleteExam;



    public ExamSubMenu(CreateExamController createExam, UpdateExamController updateExam, DeleteExamController deleteExam) {
        this.createExam = createExam;
        this.updateExam = updateExam;
        this.deleteExam = deleteExam;
    }
    Menu instructorMenu = new Menu("Manage exam controller");

    private void addController() {
        while(true){
            System.out.print("1. Add new exam: ");
            System.out.print("2. Add new question to exam: ");
            System.out.print("3. Add new choice to question: ");
            System.out.println("4. Back to main menu");
            int choice = Inputter.getAnInteger("Input your choice:", "Invalid input",1, 4);

            switch (choice) {
                case 1:
                    try {
                        createExam.addExam();
                        
                    } catch (SQLException e) {
                        System.out.println("Failed adding exam: " + e.getMessage());
                    }
                    break;
                case 2:
                    try {
                        createExam.addQuestion();
                    } catch (SQLException e) {
                        System.out.println("Failed adding question: " + e.getMessage());
                    }
                    break;
                case 3:
                    try {
                        int questionID = Inputter.getAnInteger("Enter the Question ID to add choices: ", "Invalid input.");
                        createExam.addChoices(questionID);
                    } catch (SQLException e) {
                        System.out.println("Falied adding choice: " + e.getMessage());
                    }
                    break;
                case 4:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid input please select (1-4)");
            }
        }
    }

   private void updateController() {
       while(true){
            System.out.println("1. Update exam: ");
            System.out.println("2. Update question: ");
            System.out.println("3. Update choice: ");
            System.out.println("4. Back to main menu");
            int choice = Inputter.getAnInteger("Input your choice:", "Invalid input",1, 4);

            switch (choice) {
                case 1:
                    try {
                        updateExam.updateExam();
                    } catch (SQLException e) {
                        System.out.println("Failed updating exam" + e.getMessage());
                    }
                    break;
                case 2:
                    try {
                        updateExam.updateQuestion();
                    } catch (SQLException e) {
                        System.out.println("Failed updating question" + e.getMessage());
                    }
                    break;
                case 3:
                    try {
                        updateExam.updateChoice();
                    } catch (SQLException e) {
                        System.out.println("Failed updating choice" + e.getMessage());
                    }
                    break;
                case 4:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid input please select (1-4)");
            }
       }
        
    }
    
    private void deleteController() {
        while(true){
            System.out.println("1. Delete exam");
            System.out.println("2. Delete question");
            System.out.println("3. Delete choice");
            int choice = Inputter.getAnInteger("Input your choice:", "Invalid input",1, 3);

            switch (choice) {
                case 1:
                    try {
                        deleteExam.deleteExam();
                    } catch (SQLException e) {
                        System.out.println("Failed deleting exam: " + e.getMessage());
                    }
                    break;
                case 2:
                    try {
                        deleteExam.deleteQuestion();
                       
                    } catch (SQLException e) {
                        System.out.println("Failed deleting question: " + e.getMessage());
                    }
                    break;
                case 3:
                    try {
                        deleteExam.deleteChoice();
                        
                    } catch (SQLException e) {
                        System.out.println("Failed deleting choice: " + e.getMessage());
                    }
                    break;
                case 4:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid input please choose (1-4)");
                    break;
            }
        }
        
    }

    public void manageExams() {
        System.out.println("1. Add exam: ");
        System.out.println("2. Update exam: ");
        System.out.println("3. Delete exam: ");
        System.out.println("");
        int choice = Inputter.getAnInteger("Input your choice:", "Invalid input",1, 3);

        switch (choice) {
            case 1:
                addController();
                break;
            case 2:
                updateController();
                break;
            case 3:
                deleteController();
                break;
        }
    }
}
