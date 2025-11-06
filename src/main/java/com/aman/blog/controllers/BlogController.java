package com.aman.blog.controllers;

import com.aman.blog.domain.dtos.BlogDto;
import com.aman.blog.domain.entities.User;
import com.aman.blog.services.BlogService;
import com.aman.blog.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;
    private final AuthUtils authUtils;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<BlogDto> createBlog(@RequestPart("blog") BlogDto blogDto,
                                              @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) throws IOException {
        System.out.println("Incoming Blog: " + blogDto);
        System.out.println("Images count: " + (images != null ? images.size() : 0));

        User user = authUtils.getCurrentUser();

        if (user == null) {
            throw new IllegalStateException("User is null — authentication missing or token invalid");
        }

        BlogDto created = blogService.createBlog(blogDto, user, images);
        return ResponseEntity.ok(created);
    }

    @PutMapping(value = "/{blogId}", consumes = {"multipart/form-data"})
    public ResponseEntity<BlogDto> updateBlog( @PathVariable Long blogId,
                                               @RequestPart("blog") BlogDto blogDto,
                                               @RequestPart(value = "images", required = false) List<MultipartFile> images)
        throws IOException {
        System.out.println("Incoming Blog: " + blogDto);

        User user = authUtils.getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("User is null — authentication missing or token invalid");
        }

        BlogDto updated = blogService.updateBlog(blogId, blogDto, user, images);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<List<BlogDto>> getAllBlogs() {
        return ResponseEntity.ok(blogService.getAllBlogs());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BlogDto>> getBlogsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(blogService.getBlogsByUser(userId));
    }

    @GetMapping("/{blogId}")
    public ResponseEntity<BlogDto> getBlogDetailsByBlogId(@PathVariable Long blogId) {
        return ResponseEntity.ok(blogService.getBlogDetailsByBlogId(blogId));
    }

    @DeleteMapping("/{blogId}")
    public ResponseEntity<BlogDto> deleteBlog(@PathVariable Long blogId) {
        return ResponseEntity.ok(blogService.deleteBlogById(blogId));

    }

}
