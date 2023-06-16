package com.example.MyBlogApplication.restcontroller;

import com.example.MyBlogApplication.entity.Comment;
import com.example.MyBlogApplication.entity.Post;
import com.example.MyBlogApplication.service.CommentService;
import com.example.MyBlogApplication.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
public class CommentRestController {

    @Autowired
    CommentService commentService;

    @Autowired
    PostService postService;

    @PostMapping(value = "/add-comment/{postId}")
    public ResponseEntity<String> addComment(@PathVariable int postId,
                                             @RequestParam(value = "name") String name,
                                             @RequestParam(value = "email") String email,
                                             @RequestParam(value = "comment") String comment) {
        Post post = postService.findPostById(postId);
        if (post == null) {
            return new ResponseEntity<>("Post with this ID does not exist", HttpStatus.BAD_REQUEST);
        }
        commentService.saveComment(post, name, email, comment);
        return new ResponseEntity<>("comment saved", HttpStatus.CREATED);
    }

    @GetMapping(value = "/get-comment/{postId}/{commentId}")
    @PreAuthorize("hasAuthority('ADMIN') or @securityService.isOwnerOfThePost(authentication, #postId)")
    public ResponseEntity<Comment> getComment(@PathVariable int postId, @PathVariable int commentId) {
        Comment comment = commentService.findCommentById(commentId);
        if (comment == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete-comment/{postId}/{commentId}")
    @PreAuthorize("hasAuthority('ADMIN') or @securityService.isOwnerOfThePost(authentication, #postId)")
    public ResponseEntity<String> deleteComment(@PathVariable int postId, @PathVariable int commentId) {
        Comment comment = commentService.findCommentById(commentId);
        if (comment == null) {
            return new ResponseEntity<>("comment with this id not exist", HttpStatus.BAD_REQUEST);
        }
        commentService.deleteCommentById(commentId);
        return new ResponseEntity<>("comment deleted with id" + commentId, HttpStatus.OK);
    }

    @PutMapping(value = "/update-comment-from-postman/{postId}")
    @PreAuthorize("hasAuthority('ADMIN') or @securityService.isOwnerOfThePost(authentication, #postId)")
    public ResponseEntity<Comment> updateComment(@PathVariable int postId,
                                                 @RequestParam(value = "commentId") int commentId,
                                                 @RequestParam(value = "name", required = false) String name,
                                                 @RequestParam(value = "email", required = false) String email,
                                                 @RequestParam(value = "commented") String newComment) {
        Comment comment = commentService.findCommentById(commentId);
        comment.setComment(newComment);
        comment.setUpdatedAt(new Date());
        if (name != null) {
            comment.setName(name);
        }
        if (email != null) {
            comment.setEmail(email);
        }
        commentService.updateTheComment(comment);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }
}
