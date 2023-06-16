package com.example.MyBlogApplication.controller;

import com.example.MyBlogApplication.entity.User;
import com.example.MyBlogApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/register-user")
    public String saveUser(@ModelAttribute User user, @RequestParam("confirm-password") String confirmPassword, Model model) {
        User existingUser = userService.findByEmail(user.getEmail());
        if (existingUser != null) {
            model.addAttribute("emailPresent", "This email Already Present, Please Try with Other email");
            return "registration";
        } else {
            if (userService.bothPasswordsSame(user.getPassword(), confirmPassword)) {
                User newUser = new User();
                newUser.setName(user.getName());
                newUser.setEmail(user.getEmail());
                newUser.setPassword(passwordEncoder.encode(user.getPassword()));
                newUser.setRole("AUTHOR");
                userService.saveUser(newUser);
            } else {
                model.addAttribute("passwordMissMatch", "Password is not same, please provide same password");
                return "registration";
            }
        }
        return "login";
    }

}