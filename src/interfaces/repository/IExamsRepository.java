/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaces.repository;

import data.Exam;
import java.util.Date;

/**
 *
 * @author NGHIA
 */
public interface IExamsRepository {
    public Exam CreateExam(String examName,int subjectID,int InstructorID,Date examDate, int Duration, int TotalMark );
    public Exam FindExamById(int examID);
}
