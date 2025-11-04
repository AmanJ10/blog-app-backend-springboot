package com.aman.blog.repositories;

import com.aman.blog.domain.entities.BlogImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogImageRepository extends JpaRepository<BlogImage, Long> {
}
