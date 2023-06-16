package com.example.MyBlogApplication.restcontroller;

import com.example.MyBlogApplication.entity.Post;
import com.example.MyBlogApplication.entity.User;
import com.example.MyBlogApplication.service.PostService;
import com.example.MyBlogApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class PostRestController {

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @GetMapping(value = "/getAllPost")
    public ResponseEntity<List<Post>> getAllPost() {
        return new ResponseEntity<>(postService.findAllPost(), HttpStatus.OK);

    }

    @GetMapping(value = "/searchField/{search}")
    public ResponseEntity<List<Post>> search(@PathVariable String search) {
        List<Post> searchedPost = postService.searchPost(search);
        return new ResponseEntity<>(searchedPost, HttpStatus.OK);
    }

    @GetMapping(value = "/filters")
    public ResponseEntity<List<Post>> filter(@RequestParam(value = "date", required = false) String date,
                                             @RequestParam(value = "authors", required = false) String authors,
                                             @RequestParam(value = "tagsIds", required = false) String tagIds) {
        List<Integer> tags = new ArrayList<>();
        List<String> filterAuthorNames = new ArrayList<>();
        if (tagIds != null && !tagIds.isEmpty()) {
            String[] tagIdArray = tagIds.split(",");
            for (String id : tagIdArray) {
                try {
                    tags.add(Integer.parseInt(id));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        if (authors != null && !authors.isEmpty()) {
            String[] authorNames = authors.split(",");
            filterAuthorNames.addAll(Arrays.asList(authorNames));
        }
        List<Post> filteredPost = postService.filterPost(date, filterAuthorNames, tags);
        return new ResponseEntity<>(filteredPost, HttpStatus.OK);
    }

    @GetMapping(value = "get-post-by-id/{postId}")
    @PreAuthorize("hasAuthority('ADMIN') or @securityService.isOwnerOfThePost(authentication, #postId)")
    public ResponseEntity<Post> getPostById(@PathVariable int postId) {
        System.out.println(postId);
        Post post = postService.findPostById(postId);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete-post-by-id/{postId}")
    @PreAuthorize("hasAuthority('ADMIN') or @securityService.isOwnerOfThePost(authentication, #postId)")
    public ResponseEntity<String> deletePost(@PathVariable int postId) {
        postService.deletePostById(postId);
        return new ResponseEntity<String>("post deleted with id " + postId, HttpStatus.OK);
    }

    @PostMapping(value = "/add-post")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('AUTHOR')")
    public ResponseEntity<String> addPost(@RequestParam(value = "title") String title,
                                          @RequestParam(value = "tags") String tags,
                                          @RequestParam(value = "content") String content,
                                          Principal principal) {
        User user = userService.findByName(principal.getName());
        postService.savePost(title, tags, content, user.getName(), user);
        return new ResponseEntity<>("post created", HttpStatus.CREATED);
    }

    @GetMapping(value = "/get-all-post-by-paginated")
    public ResponseEntity<List<Post>> getPostsByPaginated(@RequestParam(value = "start") int pageNumber,
                                                          @RequestParam(value = "limit") int pageSize) {
        return new ResponseEntity<List<Post>>(postService.getAllPaginatedPosts(pageNumber, pageSize), HttpStatus.OK);
    }

    @PutMapping(value = "/update=post-from-postman/{postId}")
    @PreAuthorize("hasAuthority('ADMIN') or @securityService.isOwnerOfThePost(authentication, #postId)")
    public ResponseEntity<Post> updatePost(@PathVariable int postId,
                                           @RequestParam(value = "title", required = false) String title,
                                           @RequestParam(value = "tags", required = false) String tags,
                                           @RequestParam(value = "content", required = false) String content) {
        Post post = postService.updateThePost(postId, title, tags, content);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }
    @GetMapping(value = "/sort")
    public ResponseEntity<List<Post>> getPostBySorting(@RequestParam(value = "sortDirection")String sortDirection,
                                                       @RequestParam(value = "sortBy")String sortField){
        List<Post> sortedPosts=postService.sortPosts(sortDirection,sortField);
        if(sortedPosts.isEmpty()){
            return  new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(sortedPosts,HttpStatus.OK);
    }
}
