package com.example.MyBlogApplication.service;

import com.example.MyBlogApplication.repository.CommentRepository;
import com.example.MyBlogApplication.entity.Comment;
import com.example.MyBlogApplication.entity.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CommentService {
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    PostService postService;

    public void saveComment(Post post, String userName, String email, String comment) {
        Comment newComment = new Comment();
        newComment.setCreatedAt(new Date());
        newComment.setName(userName);
        newComment.setEmail(email);
        newComment.setComment(comment);
        newComment.setPosts(post);
        commentRepository.save(newComment);
    }

    public List<Comment> getAllCommentsByPostId(int id) {
        List<Comment> postComments = new ArrayList<>();
        List<Comment> allComments = commentRepository.findAll();
        for (Comment comment : allComments) {
            if (comment.getPosts() == null) {
                continue;
            }
            if (comment.getPosts().getId() == id) {
                postComments.add(comment);
            }
        }
        return postComments;
    }

    public void deleteCommentById(int commentId) {
        commentRepository.deleteById(commentId);
    }

    public Comment findCommentById(int commentId) {
        Comment comment = commentRepository.findById(commentId).get();
        return comment;
    }

    public void saveUpdatedComment(int commentId, String comment, int postId) {
        Comment oldComment = findCommentById(commentId);
        oldComment.setComment(comment);
        oldComment.setUpdatedAt(new Date());
        Post post = postService.findPostById(postId);
        oldComment.setPosts(post);
        commentRepository.save(oldComment);
    }

    public void updateTheComment(Comment comment) {
        commentRepository.save(comment);
    }
}