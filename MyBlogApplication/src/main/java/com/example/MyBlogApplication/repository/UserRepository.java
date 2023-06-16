package com.example.MyBlogApplication.repository;

import com.example.MyBlogApplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

    @Query("select user from User user where user.role = 'USER'")
    List<User> findAllUsers();

    User findByEmail(String username);

    User findByName(String name);
}