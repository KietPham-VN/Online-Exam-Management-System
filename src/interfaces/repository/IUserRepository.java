/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaces.repository;

import data.User;

/**
 *
 * @author NGHIA
 */
public interface IUserRepository {
    public User CreateUser(String userName,String password, String email, String role);
    public User FindUserById(int UserID);
}
