package com.example.MyBlogApplication.service;

import com.example.MyBlogApplication.entity.User;
import com.example.MyBlogApplication.repository.PostRepository;
import com.example.MyBlogApplication.entity.Post;
import com.example.MyBlogApplication.entity.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PostService {
    @Autowired
    PostRepository postRepository;
    @Autowired
    TagService tagService;

    public void savePost(String title, String tags, String content, String author, User user) {
        Post post = new Post();
        post.setAuthor(author);
        post.setPublishedAt(new Date());
        post.setCreatedAt(new Date());
        post.setUpdatedAt(new Date());
        post.setPublished(true);
        post.setTitle(title);
        post.setContent(content);
        post.setUser(user);
        if (post.getContent().length() > 100) {
            String excerpt = post.getContent().substring(0, 100);
            post.setExcerpt(excerpt);
        } else {
            post.setExcerpt(content);
        }
        String[] postTags = tags.split(",");
        post.setTagList(tagService.saveTags(postTags));
        postRepository.save(post);
    }

    public Post findPostById(int postId) {
        Post post = postRepository.findById(postId).get();
        return post;
    }

    public Page<Post> findPostWithPagination(int pageNo, int pageSize, String sortField, String sortDirection, String searchField) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Page<Post> posts = postRepository.searchPosts(PageRequest.of(pageNo, pageSize, sort), searchField);
        return posts;
    }

    public void deletePostById(int postId) {
        Post post = findPostById(postId);
        List<Tag> tagList = post.getTagList();
        post.setTagList(null);
        postRepository.save(post);
        tagService.deleteTags(tagList);
        postRepository.delete(post);
    }

    public void updatePost(Post post, int postId, String tagList) {
        Post existingPost = postRepository.findById(postId).get();
        existingPost.setTitle(post.getTitle());
        existingPost.setContent(post.getContent());
        existingPost.setUpdatedAt(new Date());
        List<Tag> existingPostTags = tagService.getAllTagsByPostId(postId);
        List<Tag> allTags = new ArrayList<>();
        String[] postTags = tagList.split(",");
        ArrayList<String> newTags = new ArrayList<>(Arrays.asList(postTags));
        for (Tag tag : existingPostTags) {
            if (newTags.contains(tag.getName())) {
                tag.setUpdatedAt(new Date());
                allTags.add(tag);
                newTags.remove(tag.getName());
            }
        }
        List<Tag> remainingNewTagsFromUpDate = new ArrayList<>();
        if (!newTags.isEmpty()) {
            for (String remainingNewTag : newTags) {
                Tag tag = new Tag();
                tag.setName(remainingNewTag);
                tag.setCreatedAt(new Date());
                tag.setUpdatedAt(new Date());
                remainingNewTagsFromUpDate.add(tag);
            }
        }
        allTags.addAll(tagService.saveAll(remainingNewTagsFromUpDate));
        existingPost.setTagList(allTags);
        postRepository.save(existingPost);
    }

    public List<Post> filterPost(String date, List<String> authors, List<Integer> tags) {
        if (date != null) {
            if (date.length() <= 1) date = null;
        }
        if (authors != null) {
            if (authors.size() == 0) authors = null;
        }
        if (tags != null) {
            if (tags.size() == 0) tags = null;
        }
        List<Post> allPost = postRepository.findAll();
        List<Post> filteredPostsByDate = new ArrayList<>();
        List<Post> filteredPosts = new ArrayList<>();
        if (date != null) {
            for (Post post : allPost) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String postDate = dateFormat.format(post.getCreatedAt());
                if (postDate.equals(date)) {
                    filteredPostsByDate.add(post);
                }
            }
            filteredPosts.addAll(filteredPostsByDate);
            if (authors != null) {
                filteredPosts.clear();
                List<Post> postsFilteredByDateAndAuthor = new ArrayList<>();
                for (Post post : filteredPostsByDate) {
                    for (String authorName : authors) {
                        if (post.getAuthor().equals(authorName) && (!postsFilteredByDateAndAuthor.contains(post))) {
                            postsFilteredByDateAndAuthor.add(post);
                        }
                    }
                }
                filteredPosts.addAll(postsFilteredByDateAndAuthor);
                if (tags != null) {
                    filteredPosts.clear();
                    List<Post> postsFilteredByDateAuthorAndTags = new ArrayList<>();
                    for (Post post : postsFilteredByDateAndAuthor) {
                        List<Tag> postTags = post.getTagList();
                        for (Tag tag : postTags) {
                            for (Integer tagId : tags) {
                                if (tag.getId() == tagId && (!postsFilteredByDateAuthorAndTags.contains(post))) {
                                    postsFilteredByDateAuthorAndTags.add(post);
                                }
                            }
                        }
                    }
                    filteredPosts.addAll(postsFilteredByDateAuthorAndTags);
                    return filteredPosts;
                }
                return filteredPosts;

            }
            if (tags != null) {
                filteredPosts.clear();
                List<Post> postsFilteredByDateAndTags = new ArrayList<>();
                for (Post post : filteredPostsByDate) {
                    List<Tag> postTags = post.getTagList();
                    for (Tag tag : postTags) {
                        for (Integer tagId : tags) {
                            if (tag.getId() == tagId && (!postsFilteredByDateAndTags.contains(post))) {
                                postsFilteredByDateAndTags.add(post);
                            }
                        }
                    }
                }
                filteredPosts.addAll(postsFilteredByDateAndTags);
                return filteredPosts;
            }
            return filteredPosts;
        }
        if (authors != null) {
            List<Post> postsFilteredByAuthor = new ArrayList<>();
            for (Post post : allPost) {
                for (String authorName : authors) {
                    if (post.getAuthor().equals(authorName) && (!postsFilteredByAuthor.contains(post))) {
                        postsFilteredByAuthor.add(post);
                    }
                }
            }
            if (tags != null) {
                List<Post> postsFilteredByAuthorAndTags = new ArrayList<>();
                for (Post post : postsFilteredByAuthor) {
                    List<Tag> postTags = post.getTagList();
                    for (Tag tag : postTags) {
                        for (Integer tagId : tags) {
                            if (tag.getId() == tagId && (!postsFilteredByAuthorAndTags.contains(post))) {
                                postsFilteredByAuthorAndTags.add(post);
                            }
                        }
                    }
                }
                return postsFilteredByAuthorAndTags;
            }
            return postsFilteredByAuthor;
        }
        List<Post> postsFilteredByTags = new ArrayList<>();
        if (tags != null) {
            for (Post post : allPost) {
                List<Tag> postTags = post.getTagList();
                for (Tag tag : postTags) {
                    for (Integer tagId : tags) {
                        if (tag.getId() == tagId && (!postsFilteredByTags.contains(post))) {
                            postsFilteredByTags.add(post);
                        }
                    }
                }
            }
            return postsFilteredByTags;
        }
        return postsFilteredByTags;
    }

    public List<Post> findAllPost() {
        List<Post> allPosts = postRepository.findAll();
        return allPosts;
    }

    public Page<Post> filterAndPaginatePosts(String date, List<String> authors, List<Integer> tags, int pageNo, int pageSize) {
        List<Post> filteredPosts = filterPost(date, authors, tags);
        int totalItems = filteredPosts.size();
        int startIndex = (pageNo - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalItems);
        List<Post> paginatedPosts = filteredPosts.subList(startIndex, endIndex);
        return new PageImpl<>(paginatedPosts, PageRequest.of(pageNo - 1, pageSize), totalItems);
    }

    public List<Post> searchPost(String search) {
        List<Post> searchedPost = postRepository.searchPost(search);
        return searchedPost;
    }

    public List<Post> getAllPaginatedPosts(int pagesNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pagesNumber, pageSize);
        return postRepository.findAll(pageable).getContent();

    }

    public Post updateThePost(int postId, String title, String tags, String content) {
        Post post = postRepository.findById(postId).get();
        Post existingPost = postRepository.findById(postId).get();
        existingPost.setTitle(title);
        existingPost.setContent(content);
        existingPost.setUpdatedAt(new Date());
        existingPost.setExcerpt(content.substring(0, 50));
        List<Tag> existingPostTags = tagService.getAllTagsByPostId(postId);
        List<Tag> allTags = new ArrayList<>();
        String[] postTags = tags.split(",");
        ArrayList<String> newTags = new ArrayList<>(Arrays.asList(postTags));
        for (Tag tag : existingPostTags) {
            if (newTags.contains(tag.getName())) {
                tag.setUpdatedAt(new Date());
                allTags.add(tag);
                newTags.remove(tag.getName());
            }
        }
        List<Tag> remainingNewTagsFromUpDate = new ArrayList<>();
        if (!newTags.isEmpty()) {
            for (String remainingNewTag : newTags) {
                Tag tag = new Tag();
                tag.setName(remainingNewTag);
                tag.setCreatedAt(new Date());
                tag.setUpdatedAt(new Date());
                remainingNewTagsFromUpDate.add(tag);
            }
        }
        allTags.addAll(tagService.saveAll(remainingNewTagsFromUpDate));
        existingPost.setTagList(allTags);
        postRepository.save(existingPost);
        return existingPost;
    }

    public List<Post> sortPosts(String sortDirection, String sortField) {
        Sort.Direction direction=sortDirection.equalsIgnoreCase("asc")? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sortConditions=Sort.by(direction,sortField);
        return postRepository.findAll(sortConditions);
    }
}