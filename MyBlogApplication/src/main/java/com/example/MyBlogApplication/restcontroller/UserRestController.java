package com.example.MyBlogApplication.restcontroller;

import com.example.MyBlogApplication.entity.User;
import com.example.MyBlogApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserRestController {

    @Autowired
    UserService userService;

    @GetMapping(value = "/get-all-user")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> allUsers = userService.findAllUser();
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    @GetMapping(value = "/get-user-by-id/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable int userId) {
        User user = userService.findUserById(userId);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete-user-by-id/{userId}")
    @PreAuthorize("hasAuthority('ADMIN') or @securityService.isOwner(authentication, #userId)")
    public ResponseEntity<String> deleteUserById(@PathVariable int userId) {
        User user = userService.deleteUserById(userId);
        return new ResponseEntity<>("userDeleted", HttpStatus.OK);
    }

    @PutMapping(value = "/update-user/{userId}")
    @PreAuthorize("hasAuthority('ADMIN') or @securityService.isOwner(authentication, #userId)")
    public ResponseEntity<?> updateUser(@PathVariable int userId,
                                        @RequestParam(value = "name", required = false) String name,
                                        @RequestParam(value = "email", required = false) String email,
                                        @RequestParam(value = "role", required = false) String role) {
        User user = userService.findUserById(userId);
        if (user == null) {
            return new ResponseEntity<>("user with this id is not exist", HttpStatus.BAD_REQUEST);
        }
        userService.updateTheUser(user, name, email, role);
        return new ResponseEntity<>("user updated", HttpStatus.OK);
    }

    @PostMapping(value = "/register-user-from-postman")
    public ResponseEntity<String> createUser(@RequestParam(value = "name", required = true) String name,
                                             @RequestParam(value = "email", required = true) String email,
                                             @RequestParam(value = "password", required = true) String password,
                                             @RequestParam(value = "rePassword", required = true) String rePassword) {
        if (!password.equals(rePassword)) {
            return new ResponseEntity<>("passwords are not same", HttpStatus.BAD_REQUEST);
        }
        List<User> allUsers = userService.findAllUser();
        for (User user : allUsers) {
            if (user.getEmail().equals(email)) {
                return new ResponseEntity<>("This email is already Exist, try with different email", HttpStatus.BAD_REQUEST);
            }
        }
        User user = userService.createUser(name, email, password);
        return new ResponseEntity<>("user registered successfully with id " + user.getId(), HttpStatus.CREATED);
    }
}
