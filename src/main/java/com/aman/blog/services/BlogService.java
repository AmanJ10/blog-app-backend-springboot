package com.aman.blog.services;

import com.aman.blog.domain.dtos.BlogDto;
import com.aman.blog.domain.entities.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BlogService {
     List<BlogDto> getAllBlogs();
     BlogDto createBlog(BlogDto blogDto, User user, List<MultipartFile> images) throws IOException;

    List<BlogDto> getBlogsByUser(Long userId);

    BlogDto getBlogDetailsByBlogId(Long blogId);

    BlogDto updateBlog(Long blogId, BlogDto blogDto, User user, List<MultipartFile> images) throws IOException;

    BlogDto deleteBlogById(Long blogId);
}
