package com.example.MyBlogApplication.controller;

import com.example.MyBlogApplication.entity.Comment;
import com.example.MyBlogApplication.entity.Post;
import com.example.MyBlogApplication.entity.Tag;
import com.example.MyBlogApplication.entity.User;
import com.example.MyBlogApplication.service.CommentService;
import com.example.MyBlogApplication.service.PostService;
import com.example.MyBlogApplication.service.TagService;
import com.example.MyBlogApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@Controller
public class PostController {
    @Autowired
    PostService postService;
    @Autowired
    CommentService commentService;
    @Autowired
    TagService tagService;

    @Autowired
    UserService userService;

    @GetMapping("/")
    public String getAllPosts(@RequestParam(value = "start", defaultValue = "1") int start,
                              @RequestParam(value = "limit", defaultValue = "10") int limit,
                              Model model, Principal principal) {
        List<Tag> allTags = tagService.findAllTags();
        model.addAttribute("allTags", allTags);
        List<String> allAuthors = userService.findAllAuthors();
        model.addAttribute("allAuthors", allAuthors);
        return findPaginated(start, limit, "asc", "", model, principal);
    }

    @RequestMapping("/register")
    public String register() {
        return "registration-form";
    }

    @GetMapping("/create-post")
    public String createPost(Principal principal) {
        if (principal == null) {
            return "access-denied";
        }
        return "add-post";
    }

    @GetMapping("/update-post{id}")
    @PreAuthorize("hasRole('ADMIN') or @postService.findPostById(#id).getAuthor() == principal.getName()")
    public String updatePost(@PathVariable("id") int id, Model model, Principal principal) {
        Post post = postService.findPostById(id);
        model.addAttribute("post", post);
        return "update-post-form";
    }

    @PostMapping("/update-post")
    @PreAuthorize("hasRole('ADMIN') or @postService.findPostById(#postId).getAuthor() == principal.getName()")
    public String updatePost(@ModelAttribute("post") Post post,
                             @RequestParam("postId") int postId,
                             @RequestParam("tags") String tagList) {
        postService.updatePost(post, postId, tagList);
        return "redirect:/";
    }

    @PostMapping("/create-post")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AUTHOR')")
    public String createPost(@RequestParam("title") String title,
                             @RequestParam("content") String content,
                             @RequestParam("allTags") String tags,
                             Principal principal) {
        User user = userService.findByName(principal.getName());
        postService.savePost(title, tags, content, user.getName(), user);
        return "redirect:/";
    }

    @GetMapping("/show-post{id}")
    public String showPostById(@PathVariable("id") int postId, Model model, Principal principal) {
        Post post = postService.findPostById(postId);
        if (principal != null) {
            User user = userService.findByName(principal.getName());
            model.addAttribute("user", user);
        }
        model.addAttribute("post", post);
        List<Tag> postTags = tagService.getAllTagsByPostId(postId);
        model.addAttribute("tags", postTags);
        List<Comment> postComments = commentService.getAllCommentsByPostId(postId);
        model.addAttribute("comments", postComments);
        return "show-post";
    }

    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo,
                                @RequestParam(value = "limit", defaultValue = "10") int limit,
                                @RequestParam(value = "sortDir", defaultValue = "ASC") String sortDirection,
                                @RequestParam(value = "search", defaultValue = "") String searchField, Model model,
                                Principal principal) {
        Boolean loggedInUser;
        String loggedInUserName = null;
        String loggedInUserRole = null;
        if (principal != null) {
            loggedInUser = Boolean.TRUE;
        } else {
            loggedInUser = Boolean.FALSE;
        }
        String sortField = "publishedAt";
        Page<Post> page = postService.findPostWithPagination(pageNo - 1, limit, sortField, sortDirection, searchField.toLowerCase());
        List<Post> posts = page.getContent();
        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("loggedInUsername", loggedInUserName);
        model.addAttribute("loggedInUserRole", loggedInUserRole);
        model.addAttribute("start", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDirection);
        model.addAttribute("postList", posts);
        List<Tag> allTags = tagService.findAllTags();
        model.addAttribute("allTags", allTags);
        List<String> allAuthors = userService.findAllAuthors();
        model.addAttribute("allAuthors", allAuthors);
        model.addAttribute("search", searchField);
        if (principal != null) {
            User user = userService.findByName(principal.getName());
            model.addAttribute("user", user);
        }
        return "all-posts";
    }

    @GetMapping("/delete-post{id}")
    @PreAuthorize("hasRole('ADMIN') or @postService.findPostById(#id).getAuthor() == principal.getName()")
    public String deletePost(@PathVariable("id") int id, Model model, Principal principal) {
        postService.deletePostById(id);
        return "redirect:/";
    }

    @GetMapping("/posts-filter")
    public String filterAndPaginatePosts(@RequestParam(value = "date", required = false) String date,
                                         @RequestParam(value = "authorName", required = false) List<String> authors,
                                         @RequestParam(value = "tagId", required = false) List<Integer> tags,
                                         @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
                                         Model model) {
        int pageSize = 10;
        Page<Post> page = postService.filterAndPaginatePosts(date, authors, tags, pageNo, pageSize);
        List<Post> posts = page.getContent();
        model.addAttribute("start", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("postList", posts);
        List<Tag> allTags = tagService.findAllTags();
        model.addAttribute("allTags", allTags);
        List<String> allAuthors = userService.findAllAuthors();
        model.addAttribute("allAuthors", allAuthors);
        model.addAttribute("presentAuthors", authors);
        model.addAttribute("presentDate", date);
        model.addAttribute("presentTags", tags);
        return "show-filtered-post";
    }
}