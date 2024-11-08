/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import controller.CreateExamController;
import controller.DeleteController;
import controller.UpdateExamController;
import java.sql.Connection;
import java.sql.SQLException;
import utils.Inputter;

/**
 *
 * @author Hoang Tran
 */
public class SubMenu {

    private Connection conn;
    private CreateExamController createExam;
    private UpdateExamController updateExam;
    private DeleteController deleteExam;

    public SubMenu() {
    }
    

    public SubMenu(Connection conn, CreateExamController createExam, UpdateExamController updateExam, DeleteController deleteExam) {
        this.conn = conn;
        this.createExam = createExam;
        this.updateExam = updateExam;
        this.deleteExam = deleteExam;
    }
    Menu instructorMenu = new Menu("Manage exam controller");

    private void addController() {
        System.out.print("1. Add new exam: ");
        System.out.print("2. Add new question to exam: ");
        System.out.print("3. Add new choice to question: ");
        System.out.println("4. Back to main menu");
        int choice = instructorMenu.getChoice();

        switch (choice) {
            case 1:
                try {
                    createExam.addExam(conn);
                    break;
                } catch (SQLException e) {
                    System.out.println("Failed adding exam: " + e.getMessage());
                }
            case 2:
                try {
                    createExam.addQuestion(conn);
                    break;
                } catch (SQLException e) {
                    System.out.println("Failed adding question: " + e.getMessage());
                }
                break;
            case 3:
                try {
                    int questionID = Inputter.getAnInteger("Enter the Question ID to add choices: ", "Invalid input.");
                    createExam.addChoices(conn, questionID);
                    break;
                } catch (SQLException e) {
                    System.out.println("Falied adding choice: " + e.getMessage());
                }
            case 4:
                System.out.println("Exiting...");
                break;
            default:
                System.out.println("Invalid input please select (1-4)");
        }
    }

   private void updateController() {
        System.out.println("1. Update exam: ");
        System.out.println("2. Update question: ");
        System.out.println("3. Update choice: ");
        System.out.println("4. Back to main menu");
        int choice = instructorMenu.getChoice();

        switch (choice) {
            case 1:
                try {
                    updateExam.updateExam(conn);
                    break;
                } catch (SQLException e) {
                    System.out.println("Failed updating exam" + e.getMessage());
                }
            case 2:
                try {
                    updateExam.updateQuestion(conn);
                    break;
                } catch (SQLException e) {
                    System.out.println("Failed updating question" + e.getMessage());
                }
            case 3:
                try {
                    updateExam.updateChoice(conn);
                    break;
                } catch (SQLException e) {
                    System.out.println("Failed updating choice" + e.getMessage());
                }
            case 4:
                System.out.println("Exiting...");
                break;
            default:
                System.out.println("Invalid input please select (1-4)");
        }
    }
    
    private void deleteController() {
        System.out.println("1. Delete exam");
        System.out.println("2. Delete question");
        System.out.println("3. Delete choice");
        int choice = instructorMenu.getChoice();
        
        switch (choice) {
            case 1:
                try {
                    deleteExam.deleteExam(conn);
                    break;
                } catch (SQLException e) {
                    System.out.println("Failed deleting exam: " + e.getMessage());
                }
            case 2:
                try {
                    deleteExam.deleteQuestion(conn);
                    break;
                } catch (SQLException e) {
                    System.out.println("Failed deleting question: " + e.getMessage());
                }
            case 3:
                try {
                    deleteExam.deleteChoice(conn);
                    break;
                } catch (SQLException e) {
                    System.out.println("Failed deleting choice: " + e.getMessage());
                }
            case 4:
                System.out.println("Exiting...");
                break;
            default:
                System.out.println("Invalid input please choose (1-4)");
                break;
        }
    }

    public void manageExams() {
        System.out.print("1. Add exam: ");
        System.out.print("2. Update exam: ");
        System.out.print("3. Delete exam: ");
        System.out.println("");
        int choice = instructorMenu.getChoice();

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
