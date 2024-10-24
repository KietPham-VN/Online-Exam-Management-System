/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

/**
 *
 * @author Hoang Tran
 */
public class ExamData {
    private final String examName;
    private final int subjectID;
    
    public ExamData(String examName, int subjectID) {
        this.examName = examName;
        this.subjectID = subjectID;
    }
    
    public String getExamName() {
        return examName;
    }
    
    public int getSubjectID() {
        return subjectID;
    }
}
