package com.aman.blog.mappers;

import com.aman.blog.domain.dtos.CategoryDto;
import com.aman.blog.domain.dtos.CreateCategoryRequest;
import com.aman.blog.domain.entities.Category;

public class CategoryMapper {

    private CategoryMapper() {
        // prevent instantiation
    }

    public static CategoryDto toDto(Category category){

        if(category == null) return null;

        return CategoryDto.builder()
                .id(category.getId())
                .categoryName(category.getCategoryName())
                .build();
    }

    public static Category toEntity(CreateCategoryRequest createCategoryRequest){
        if(createCategoryRequest == null) return null;

        return Category.builder()
                .categoryName(createCategoryRequest.getCategoryName())
                .build();
    }

}
