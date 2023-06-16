package com.example.MyBlogApplication.controller;

import com.example.MyBlogApplication.entity.Comment;
import com.example.MyBlogApplication.entity.Post;
import com.example.MyBlogApplication.service.CommentService;
import com.example.MyBlogApplication.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class CommentController {
    @Autowired
    CommentService commentService;
    @Autowired
    PostService postService;

    @PostMapping("/post{id}/addComment")
    public String addComment(@PathVariable("id") int postId,
                             @RequestParam("name") String commentName,
                             @RequestParam("email") String email,
                             @RequestParam("comment") String comment) {
        Post post = postService.findPostById(postId);
        commentService.saveComment(post, commentName, email, comment);
        return "redirect:/show-post" + postId;
    }

    @GetMapping("/delete-comment{id}")
    @PreAuthorize("hasRole('ADMIN') or @postService.findPostById(#postId).getAuthor() == principal.getName()")
    public String deleteComment(@PathVariable("id") int commentId, @RequestParam("postId") int postId, Principal principal) {
        commentService.deleteCommentById(commentId);
        return "redirect:/show-post" + postId;
    }

    @GetMapping("/update-comment{id}")
    @PreAuthorize("hasRole('ADMIN') or @postService.findPostById(#postId).getAuthor() == principal.getName()")
    public String updateComment(@PathVariable("id") int commentId, @RequestParam("postId") int postId, Model model, Principal principal) {
        Comment comment = commentService.findCommentById(commentId);
        model.addAttribute("commentAttribute", comment);
        Post post = postService.findPostById(postId);
        model.addAttribute("post", post);
        return "update-comment";
    }

    @PostMapping("/update-comment")
    @PreAuthorize("hasRole('ADMIN') or @postService.findPostById(#postId).getAuthor() == principal.getName()")
    public String updateComment(@RequestParam("comment") String comment,
                                @RequestParam("commentId") int commentId,
                                @RequestParam("postId") int postId,
                                Principal principal) {
        commentService.saveUpdatedComment(commentId, comment, postId);
        return "redirect:/show-post" + postId;
    }
}