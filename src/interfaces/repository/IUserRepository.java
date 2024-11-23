/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaces.repository;

import data.GradeReport;
import data.User;
import java.util.ArrayList;

/**
 *
 * @author NGHIA
 */
public interface IUserRepository {
    public User CreateUser(String userName,String password, String email, String role);
    public User Login(String userName,String password);
    public User FindUserById(int UserID);
    public ArrayList<User> FindUsers(String userName);
    public User FindSingleUser(String userName);
    public int ResetPasword(int userID,String newPassword);
    public ArrayList<GradeReport> FindStudentGrade(int userID);
}
