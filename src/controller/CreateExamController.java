/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.time.LocalDate;
import java.util.Date;
import ui.Menu;
import utils.Inputter;

/**
 *
 * @author NGHIA
 */
public class CreateExamController {
    //Pass in the exam model here
    public CreateExamController(){}
    
    public void print(){
        while(true){
            String examName = Inputter.getString("Enter the exam name: ","Exam name must not be empty");
            int subjectID = Inputter.getAnInteger("Enter the subjectID of the exam: ","SubjectID must not be an integer");
            //Write logic for checking if subjectID is a valid ID. And ask to enter again

            Date examDate = Inputter.getDate("dd-mm-yyyy","Enter the exam date: ",new Date(),null);
            int duration = Inputter.getAnIntegerWithLowerBound("Enter the duration of the exam (minute): ","Duration must be a positive integer",1);
            int mark = Inputter.getAnIntegerWithLowerBound("Enter the mark for the exam: ","The mark must be a positive integer",1);
            //Add the exam here.

            if(!Menu.isContinue("Do you want to create more exams (y/n)?")) return;
        }
    }
}
