package com.example.MyBlogApplication.service;

import com.example.MyBlogApplication.entity.Post;
import com.example.MyBlogApplication.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    @Autowired
    UserService userService;

    @Autowired
    PostService postService;

    public boolean isOwner(Authentication authentication, int userId) {
        String name = authentication.getName();
        User user = userService.findUserById(userId);
        return user != null && user.getName().equals(name);
    }

    public boolean isOwnerOfThePost(Authentication authentication, int postId) {
        String name = authentication.getName();
        Post post = postService.findPostById(postId);
        User user = userService.findUserById(post.getUser().getId());
        return user != null && user.getName().equals(name);
    }
}
