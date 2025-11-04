package com.aman.blog.services;

import com.aman.blog.domain.dtos.CategoryDto;
import com.aman.blog.domain.dtos.CreateCategoryRequest;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> listCategories();

    CategoryDto createCategory(CreateCategoryRequest createCategoryRequest);
}
