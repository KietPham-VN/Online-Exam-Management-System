/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import data.Choice;
import data.Exam;
import data.Question;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import repository.ExamRepository;
import ui.Menu;
import utils.Inputter;
import utils.StringProcessor;

/**
 *
 * @author NGHIA
 */
public class UpdateExamController {
    private final ExamRepository examRepository;
    private final Connection conn;

    public UpdateExamController(Connection conn) {
        this.examRepository = new ExamRepository();
        this.conn = conn;
    }

    public void updateExam() throws SQLException {
        int examID = Inputter.getAnInteger("Enter the Exam ID to update: ", "Exam ID cannot be empty");
        Exam existedExam = examRepository.findExam(conn, examID);
        if (existedExam==null) {
            System.out.println("Exam not found.");
            return;
        }


        String examNameInput = Inputter.getString("Enter new Exam Name or press enter to keep the old value: ");
        String examName = examNameInput.isEmpty() ? existedExam.getExamName() : examNameInput;

        int subjectID = existedExam.getSubjectID();
        while(true){
            String subjectIDText = Inputter.getString("Enter new Subject ID or press enter to keep the old value: ",
                                               "Subject ID must be a valid number or empty", "^\\d*$");
            if(!subjectIDText.isEmpty()){
                if(!examRepository.checkSubjectExists(conn, Integer.parseInt(subjectIDText))){
                    System.out.println("Subject not found");
                    continue;
                }
            }
            else subjectID = Integer.parseInt(subjectIDText);
            break;
        }
        
        Date examDate = existedExam.getExamDate();
        while(true){
            String examDateInput = Inputter.getString("Enter new Exam Date (yyyy-MM-dd) or press enter to keep the old value: ","Please enter the date in the following format: yyyy-MM-dd","^(\\d{4}-\\d{2}-\\d{2})?$");
            if(!examDateInput.isEmpty()){
                examDate = StringProcessor.parseDate(examDateInput, "yyyy-MM-dd");
                if(examDate == null||examDate.before(new Date())){
                    System.out.println("Please enter a valid date that is earlier than today");
                    continue;
                }
            }
            
            break;
        }
        
        int duration = existedExam.getDuration();
        while(true){
            String durationInput = Inputter.getString("Enter new Duration (minutes) or press enter to keep the old value: ",
                                                  "Duration must be a valid number or empty", "^\\d*$");
            duration =  Integer.parseInt(durationInput);    
            if (duration <= 0 && !durationInput.isEmpty()) {
                System.out.println("Duration must be greater than 0");
            }
            else break;
        }

        int totalMarks = existedExam.getTotalMarks();
        while(true){
            String totalMarksInput = Inputter.getString("Enter new Total Marks or press enter to keep the old value: ",
                                                "Total Marks must be a valid number or empty", "^\\d*$");
            totalMarks = Integer.parseInt(totalMarksInput);
            if (totalMarks <= 0 && !totalMarksInput.isEmpty()) {
                System.out.println("Total Marks must be greater than 0.");
            }   
            else break;
        }

        updateQuestionMarks(totalMarks,examID);

        Exam updatedExam = new Exam(examID, examName, subjectID, existedExam.getInstructorID(), new java.sql.Date(examDate.getTime()), duration, totalMarks);
        examRepository.updateExam(conn, examID, updatedExam);
        System.out.println("Exam updated successfully.");
    }

    public void updateQuestion() throws SQLException {
        int questionID = Inputter.getAnInteger("Enter the Question ID to update: ", "Question ID cannot be empty");
        Question existedQuestion = examRepository.findQuestion(conn, questionID);
        if (existedQuestion==null) {
            System.out.println("Question not found.");
            return;
        }

         String questionText = Inputter.getString("Enter new Question Text or press enter to keep the old value: ");
        if (questionText.isEmpty()) {
            questionText = existedQuestion.getQuestionText();
        }

        String questionType = Inputter.getString("Enter new Question Type [MCQ|ShortAnswer] or press enter to keep the old value: ",
                                             "Please enter MCQ or ShortAnswer or leave empty", "^(MCQ|ShortAnswer)?$");
        if (questionType.isEmpty()) {
            questionType = existedQuestion.getQuestionType();
        }

        int marks = existedQuestion.getMarks();
        while (true) {
            String marksInput = Inputter.getString("Enter new Marks or press enter to keep the old value: ",
                                                   "Marks must be a valid number greater than 0 or empty", "^\\d*$");
            if (!marksInput.isEmpty()) {
                int newMarks = Integer.parseInt(marksInput);
                if (marks <= 0) {
                    System.out.println("Marks must be greater than 0.");
                    continue;
                }
                else marks = newMarks;
            }
            break;
        }

        int subjectID = existedQuestion.getSubjectID();
        while (true) {
            String subjectIDText = Inputter.getString("Enter new Subject ID or press enter to keep the old value: ",
                                                      "Subject ID must be a valid number or empty", "^\\d*$");
            if (!subjectIDText.isEmpty()) {
                int newSubjectID = Integer.parseInt(subjectIDText);
                if (!examRepository.checkSubjectExists(conn, newSubjectID)) {
                    System.out.println("Subject not found.");
                    continue;
                }
                else subjectID = newSubjectID;
            }
            break;
        }

        Question updatedQuestion = new Question(questionID, questionText, questionType, marks, existedQuestion.getExamID(), subjectID);
        examRepository.updateQuestion(conn, questionID, updatedQuestion);
        System.out.println("Question updated successfully.");
    }

    public void updateChoice() throws SQLException {
        int choiceID = Inputter.getAnInteger("Enter the Choice ID to update: ", "Choice ID cannot be empty");
        Choice existedChoice = examRepository.findChoice(conn, choiceID);
        if (existedChoice==null) {
            System.out.println("Choice not found.");
            return;
        }

        String choiceText = Inputter.getString("Enter new Choice Text or press enter to keep the old value: ");
        if(choiceText.isEmpty()) choiceText = existedChoice.getChoiceText();
        boolean isCorrect = Menu.isContinue("Is this choice correct? [Y/N]: ");

        examRepository.updateChoice(conn, choiceID, choiceText, isCorrect);
        System.out.println("Choice updated successfully.");
    }
    
    private void updateQuestionMarks(int totalMark,int examId) throws SQLException{
        while(true){
            ArrayList<Question> questions = examRepository.findQuestionsInExam(conn, examId);
            int currMarks = questions.stream().mapToInt(q->q.getMarks()).sum();
            System.out.println("Total mark: "+currMarks+"\\"+totalMark);
            System.out.println("0. Exit");
            for(int i=0;i<questions.size();i++){
                System.out.println((i+1)+". Mark: "+questions.get(i).getMarks()+"; QuestionText: "+questions.get(i).getQuestionText());
            }
            int choice = Inputter.getAnInteger("Enter the question you want to change the mark of: ", "Please enter a number from 0 to "+questions.size(), 0, questions.size()) -1;
            
            if(choice>-1){
                Question choosenQuestion = questions.get(choice-1);
                System.out.println("Old marks: "+choosenQuestion.getMarks());
                int marks = Inputter.getAnInteger("Enter new Marks: ","Marks must be a valid number between 1 and 10 or empty", 1,10);
                choosenQuestion.setMarks(marks);
                examRepository.updateQuestion(conn, examId, choosenQuestion);
            }
            else{
                if(currMarks!=totalMark){
                    System.out.println("The total mark currently of all questions currently is not equal to that of the exam. Please adjust the mark of the question accordingly");
                    continue;
                }
                break;
            }
        }
    }
}
