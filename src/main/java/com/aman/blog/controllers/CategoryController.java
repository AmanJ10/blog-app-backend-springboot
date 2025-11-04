package com.aman.blog.controllers;

import com.aman.blog.domain.dtos.CategoryDto;
import com.aman.blog.domain.dtos.CreateCategoryRequest;
import com.aman.blog.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> listCategories(){
        return ResponseEntity.ok(categoryService.listCategories());
    }

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CreateCategoryRequest createCategoryRequest){
        CategoryDto categoryDto = categoryService.createCategory(createCategoryRequest);

        // Return 201 Created + Location header
        return ResponseEntity
                .created(URI.create("/api/v1/categories/" + categoryDto.getId()))
                .body(categoryDto);
    }
}
