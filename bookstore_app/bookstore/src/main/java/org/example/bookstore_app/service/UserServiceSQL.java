package org.example.bookstore_app.service;

import org.example.bookstore_app.dao.UserDAO;
import org.example.bookstore_app.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceSQL extends GenericServiceImpl<User, Integer, UserDAO>
        implements IUserService, UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceSQL(UserDAO userDAO) {
        super(userDAO);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return defaultRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().replace("ROLE_", ""))
                .disabled(!user.isEnabled())
                .build();
    }

    @Override
    @Transactional
    public User registerNewUser(String username, String rawPassword, String role) {
        if (findByUsername(username) != null) {
            throw new IllegalArgumentException("Пользователь с таким именем уже существует");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setEnabled(true);
        save(user);
        return user;
    }
}