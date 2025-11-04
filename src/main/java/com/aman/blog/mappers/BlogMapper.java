package com.aman.blog.mappers;

import com.aman.blog.domain.dtos.AuthorDto;
import com.aman.blog.domain.dtos.BlogDto;
import com.aman.blog.domain.dtos.ImageDto;
import com.aman.blog.domain.entities.Blog;
import com.aman.blog.domain.entities.BlogImage;
import com.aman.blog.domain.entities.Category;
import com.aman.blog.domain.entities.User;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class BlogMapper {

    private BlogMapper() {
        // prevent instantiation
    }

    public static BlogDto toDto(Blog blog) {
        if(blog == null) return null;

        return BlogDto.builder()
                .id(blog.getId())
                .author(toAuthorDto(blog.getAuthor()))
                .title((blog.getTitle()))
                .content((blog.getContent()))
                .createdAt((blog.getCreatedAt()))
                .updatedAt(null) // You can add an updatedAt field to Blog later
                .categories(blog.getCategories() != null ? blog.getCategories()
                        .stream()
                        .map(CategoryMapper::toDto)
                        .collect(Collectors.toList())
                        : null)
                .images(blog.getImages() != null ? blog.getImages()
                        .stream()
                        .map(BlogMapper::toImageDto)
                        .collect(Collectors.toList())
                        : null)
                .build();
    }

    // ---------- DTO + USER + CATEGORIES + IMAGES → ENTITY ----------
    public static Blog toEntity(BlogDto blogDto, User user, List<Category> categories, List<String> imageUrls) {
        if (blogDto == null) return null;

        Blog blog = Blog.builder()
                .author(user)
                .title(blogDto.getTitle())
                .content(blogDto.getContent())
                .categories(categories)
                .build();

        List<BlogImage> blogImages = imageUrls != null
                ? imageUrls.stream()
                .map(url -> BlogImage.builder()
                        .blog(blog)
                        .imageUrl(url)
                        .build())
                .collect(Collectors.toList())
                : null;

        blog.setImages(blogImages);
        return blog;
    }

    // Convert author entity to AuthorDto (simple representation of User)
    private static AuthorDto toAuthorDto(User user) {
        if(user == null) return null;

        return AuthorDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    // Convert BlogImage → ImageDto
    private static ImageDto toImageDto(BlogImage blogImage) {
        if(blogImage == null) return null;

        return ImageDto.builder()
                .id(blogImage.getId())
                .imageUrl(blogImage.getImageUrl())
                .build();
    }
}
