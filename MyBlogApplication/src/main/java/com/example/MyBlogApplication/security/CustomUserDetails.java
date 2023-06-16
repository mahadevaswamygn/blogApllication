package com.example.MyBlogApplication.security;

import com.example.MyBlogApplication.entity.User;
import com.example.MyBlogApplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;


@Service
public class CustomUserDetails implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User validUser = userRepository.findByEmail(username);
        if (validUser == null) {
            throw new UsernameNotFoundException("User doesn't exist");
        }
        return new org.springframework.security.core.userdetails.User(
                validUser.getName(),
                validUser.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(validUser.getRole()))
        );
    }
}
