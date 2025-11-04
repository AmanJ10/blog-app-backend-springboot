package com.aman.blog.repositories;

import com.aman.blog.domain.entities.Blog;
import com.aman.blog.domain.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog, Long> {
    List<Blog> findAllByCategoriesContaining(Category category);
    List<Blog> findAllByAuthor_Id(Long userId);
}
