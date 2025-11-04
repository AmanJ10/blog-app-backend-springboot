package com.aman.blog.services.impl;

import com.aman.blog.domain.dtos.BlogDto;
import com.aman.blog.domain.dtos.CategoryDto;
import com.aman.blog.domain.entities.Blog;
import com.aman.blog.domain.entities.BlogImage;
import com.aman.blog.domain.entities.Category;
import com.aman.blog.domain.entities.User;
import com.aman.blog.exceptions.ResourceNotFoundException;
import com.aman.blog.mappers.BlogMapper;
import com.aman.blog.repositories.BlogRepository;
import com.aman.blog.repositories.CategoryRepository;
import com.aman.blog.repositories.UserRepository;
import com.aman.blog.services.AuthenticationService;
import com.aman.blog.services.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;


    private final FirebaseStorageService firebaseStorageService;

    @Transactional
    public BlogDto createBlog(BlogDto blogDto, User user, List<MultipartFile> images) throws IOException {
        List<String> imageUrls = new ArrayList<>();

        for(MultipartFile file : images){
            String imageUrl = firebaseStorageService.uploadFile(file, "spring-boot-blog-images");
            imageUrls.add(imageUrl);
        }

        List<Category> categories = categoryRepository.findAllById(
                blogDto.getCategories()
                        .stream()
                        .map(CategoryDto::getId)
                        .collect(Collectors.toList())
        );

        Blog blog = BlogMapper.toEntity(blogDto, user, categories,  imageUrls);
        Blog saved = blogRepository.save(blog);

        return BlogMapper.toDto(saved);
    }


    @Override
    public List<BlogDto> getAllBlogs() {
        return blogRepository.findAll()
                .stream()
                .map(BlogMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BlogDto> getBlogsByUser(Long userId) {
        return blogRepository.findAllByAuthor_Id(userId)
                .stream()
                .map(BlogMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public BlogDto getBlogDetailsByBlogId(Long blogId) {
        return blogRepository.findById(blogId)
                .map(BlogMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with id " + blogId));
    }

    @Transactional
    @Override
    public BlogDto updateBlog(Long blogId, BlogDto blogDto, User user, List<MultipartFile> images) throws IOException{
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with id " + blogId));

        if(!blog.getAuthor().getId().equals(user.getId())){
            throw new SecurityException("You are not allowed to update this blog");
        }

        blog.setTitle(blogDto.getTitle());
        blog.setContent(blogDto.getContent());

        if (blogDto.getCategories() != null && !blogDto.getCategories().isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(
                    blogDto.getCategories()
                            .stream()
                            .map(CategoryDto::getId)
                            .collect(Collectors.toList())
            );
            blog.setCategories(categories);
        }

        List<BlogImage> existingImages = blog.getImages();

// Remove images no longer present in DTO
        if (blogDto.getImages() != null) {
            existingImages.removeIf(img -> {
                boolean toRemove = blogDto.getImages().stream()
                        .noneMatch(dto -> dto.getId().equals(img.getId()));

                if (toRemove) {
                    firebaseStorageService.deleteFileByUrl(img.getImageUrl());
                }

                return toRemove;
            });
        }

    // Add new ones
        if (images != null && !images.isEmpty()) {
            for (MultipartFile file : images) {
                String imageUrl = firebaseStorageService.uploadFile(file, "spring-boot-blog-images");
                BlogImage blogImage = new BlogImage();
                blogImage.setImageUrl(imageUrl);
                blogImage.setBlog(blog);
                existingImages.add(blogImage);
            }
        }


        Blog updated = blogRepository.save(blog);
        return BlogMapper.toDto(updated);
    }

    @Transactional
    @Override
    public BlogDto deleteBlogById(Long blogId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof UserDetails userDetails)) {
            throw new SecurityException("Unauthorized");
        }

        String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with id " + blogId));

        if(!blog.getAuthor().getId().equals(user.getId())){
            throw new SecurityException("You are not allowed to delete this blog");
        }

        // Delete associated images from Firebase
        if (blog.getImages() != null && !blog.getImages().isEmpty()) {
            for (BlogImage image : blog.getImages()) {
                try {
                    firebaseStorageService.deleteFileByUrl(image.getImageUrl());
                } catch (Exception e) {
                    System.err.println("Failed to delete image: " + image.getImageUrl());
                }
            }
        }

        // Delete the blog
        blogRepository.delete(blog);

        return BlogMapper.toDto(blog);
    }

}
