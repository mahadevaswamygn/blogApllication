package com.example.MyBlogApplication.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    @Autowired
    CustomUserDetails customUserDetails;

    private static final String[] WHITE_LIST_URLS = {
            "/", "/login", "/getAllPost", "/searchField/{search}", "/show-post{id}", "/register", "/register-user", "/post{Id}/addComment", "/posts-filter"
            , "/page/{pageNo}", "/filters", "/getPostById/{postId}", "/add-comment",
            "/get-all-post-by-paginated", "/get-all-user","/sort"};

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(configure ->
                        configure
                                .requestMatchers(WHITE_LIST_URLS)
                                .permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(form ->
                        form
                                .loginPage("/login")
                                .loginProcessingUrl("/authenticateTheUser")
                                .defaultSuccessUrl("/")
                                .permitAll()
                ).logout(logout ->
                        logout
                                .permitAll()
                ).csrf(AbstractHttpConfigurer::disable);
        return httpSecurity.build();
    }
}
