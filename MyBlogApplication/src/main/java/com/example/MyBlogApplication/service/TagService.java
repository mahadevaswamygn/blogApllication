package com.example.MyBlogApplication.service;

import com.example.MyBlogApplication.repository.TagRepository;
import com.example.MyBlogApplication.entity.Post;
import com.example.MyBlogApplication.entity.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TagService {
    @Autowired
    TagRepository tagRepository;
    @Autowired
    @Lazy
    PostService postService;

    public List<Tag> saveTags(String[] tagNames) {
        List<Tag> tags = new ArrayList<>();
        List<Tag> existingTags = new ArrayList<>();
        for (String tagName : tagNames) {
            Tag tag = tagRepository.findByName(tagName);
            if (tag == null) {
                Tag newTag = new Tag();
                newTag.setName(tagName);
                newTag.setCreatedAt(new Date());
                newTag.setUpdatedAt(new Date());
                tags.add(newTag);
            } else {
                existingTags.add(tag);
            }
        }
        tagRepository.saveAll(tags);
        existingTags.addAll(tags);
        List<Tag> allTags = existingTags;
        return allTags;
    }

    public List<Tag> getAllTagsByPostId(int postId) {
        List<Tag> postTags = new ArrayList<>();
        List<Tag> allTags = tagRepository.findAll();
        Post post = postService.findPostById(postId);
        for (Tag tag : allTags) {
            if (post.getTagList().contains(tag)) {
                postTags.add(tag);
            }
        }
        return postTags;
    }

    public List<Tag> findAllTags() {
        List<Tag> allTags = tagRepository.findAll();
        return allTags;
    }

    public void deleteTags(List<Tag> tagList) {
        for (Tag tag : tagList) {
            try {
                tagRepository.delete(tag);
            } catch (Exception e) {
                System.out.println("Exception-> The tag " + tag.getName() + "you can not delete because this tag mapped with other post ");
            }
        }
    }

    public List<Tag> saveAll(List<Tag> remainingNewTagsFromUpDate) {
        return tagRepository.saveAll(remainingNewTagsFromUpDate);
    }
}