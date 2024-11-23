/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

/**
 *
 * @author NGHIA
 */
public class GradeReport {
    private int score;
    private String examName;

    public GradeReport(int score, String examName) {
        this.score = score;
        this.examName = examName;
    }

    public int getScore() {
        return score;
    }

    public String getExamName() {
        return examName;
    }
    
    
}
