package org.example.bookstore_app.controller.rest;

import org.example.bookstore_app.dto.RegisterRequestDTO;
import org.example.bookstore_app.model.User;
import org.example.bookstore_app.security.JwtTokenUtil;
import org.example.bookstore_app.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private IUserService userService;

    /**
     * Аутентификация пользователя и получение JWT-токена
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtTokenUtil.generateToken(userDetails);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Неверные учетные данные"));
        }
    }

    /**
     * Регистрация нового пользователя
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO registerRequest) {
        try {
             if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isEmpty() ||
                    registerRequest.getPassword() == null || registerRequest.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Имя пользователя и пароль не могут быть пустыми"));
            }

            User newUser = userService.registerNewUser(
                    registerRequest.getUsername().trim(),
                    registerRequest.getPassword(),
                    "ROLE_USER"
            );

            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                    .username(newUser.getUsername())
                    .password(newUser.getPassword())
                    .roles("USER")
                    .build();
            String token = jwtTokenUtil.generateToken(userDetails);

            return ResponseEntity.ok(Map.of(
                    "message", "Пользователь успешно зарегистрирован",
                    "token", token
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при регистрации: " + e.getMessage()));
        }
    }

    /**
     * Внутренний класс для запроса логина
     */
    public static class AuthRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}