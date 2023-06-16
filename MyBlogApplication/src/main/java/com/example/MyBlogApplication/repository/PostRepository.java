package com.example.MyBlogApplication.repository;

import com.example.MyBlogApplication.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query("SELECT DISTINCT post FROM Post post " +
            "LEFT JOIN post.tagList tag " +
            "WHERE " +
            "LOWER(post.title) LIKE CONCAT('%', :searchField, '%') OR " +
            "LOWER(post.content) LIKE CONCAT('%', :searchField, '%') OR " +
            "LOWER(post.author) LIKE CONCAT('%', :searchField, '%') OR " +
            "LOWER(tag.name) LIKE CONCAT('%', :searchField, '%')")
    Page<Post> searchPosts(Pageable pageable, String searchField);

    @Query("SELECT DISTINCT post FROM Post post " +
            "LEFT JOIN post.tagList tag " +
            "WHERE " +
            "LOWER(post.title) LIKE CONCAT('%', :searchField, '%') OR " +
            "LOWER(post.content) LIKE CONCAT('%', :searchField, '%') OR " +
            "LOWER(post.author) LIKE CONCAT('%', :searchField, '%') OR " +
            "LOWER(tag.name) LIKE CONCAT('%', :searchField, '%')")
    List<Post> searchPost(String searchField);

}