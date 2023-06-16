package com.example.MyBlogApplication.repository;

import com.example.MyBlogApplication.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag,Integer> {

    Tag findByName(String tagName);
}