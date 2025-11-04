package com.aman.blog.services.impl;

import com.aman.blog.domain.dtos.CategoryDto;
import com.aman.blog.domain.dtos.CreateCategoryRequest;
import com.aman.blog.domain.entities.Category;
import com.aman.blog.mappers.CategoryMapper;
import com.aman.blog.repositories.CategoryRepository;
import com.aman.blog.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> listCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryDto createCategory(CreateCategoryRequest createCategoryRequest){
        // Convert CreateCategoryRequest DTO to Entity
        Category category = Category.builder()
                .categoryName(createCategoryRequest.getCategoryName())
                .build();

        if(categoryRepository.existsByCategoryNameIgnoreCase(category.getCategoryName()))
        {
            throw new IllegalArgumentException("Category already exists:" + createCategoryRequest.getCategoryName());
        }
       Category saved =  categoryRepository.save(category);
        return CategoryMapper.toDto(saved);
    }


}
