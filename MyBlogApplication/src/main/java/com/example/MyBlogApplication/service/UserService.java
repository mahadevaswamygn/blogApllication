package com.example.MyBlogApplication.service;

import com.example.MyBlogApplication.repository.UserRepository;
import com.example.MyBlogApplication.entity.Post;
import com.example.MyBlogApplication.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PostService postService;

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public boolean bothPasswordsSame(String password, String reEnteredPassword) {
        if (password.equals(reEnteredPassword)) {
            return true;
        }
        return false;
    }

    public List<String> findAllAuthors() {
        List<Post> allPosts = postService.findAllPost();
        List<String> allAuthors = new ArrayList<>();
        for (Post post : allPosts) {
            if (!allAuthors.contains(post.getAuthor())) {
                allAuthors.add(post.getAuthor());
            }
        }
        return allAuthors;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findByName(String name) {
        return userRepository.findByName(name);
    }

    public List<User> findAllUser() {
        List<User> allUsers = userRepository.findAll();
        return allUsers;
    }

    public User findUserById(int userId) {
        User user = userRepository.findById(userId).get();
        return user;
    }

    public User deleteUserById(int userId) {
        User user = userRepository.findById(userId).get();
        if (user != null) {
            userRepository.deleteById(userId);
            return user;
        }
        return user;
    }

    public void updateTheUser(User user, String name, String email, String role) {
        if (email != null) {
            user.setEmail(email);
        }
        if (name != null) {
            user.setName(name);
        }
        if (role != null) {
            user.setRole(role);
        }
        userRepository.save(user);
    }

    public User createUser(String name, String email, String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole("AUTHOR");
        userRepository.save(user);
        return user;
    }
}