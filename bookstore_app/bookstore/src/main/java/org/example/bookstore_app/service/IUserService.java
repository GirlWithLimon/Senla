package org.example.bookstore_app.service;

import org.example.bookstore_app.model.User;

public interface IUserService extends GenericService<User, Integer> {
    User findByUsername(String username);
    User registerNewUser(String username, String rawPassword, String role);
}