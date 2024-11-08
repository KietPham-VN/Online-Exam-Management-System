/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package runtime;

import utils.Inputter;

/**
 *
 * @author Hoang Tran
 */
public class MainMenu {

    public static int getStartUpChoice() {
        int choice;
        System.out.println("1. Login");
        System.out.println("2. Register");
        do {
            choice = Inputter.getAnInteger("Enter your choice: ", "Invalid input", 1, 2);
        } while (choice != 1 && choice != 2);

        return choice;
    }
    
}
