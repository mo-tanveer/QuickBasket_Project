package com.basket.category.controller;

import com.basket.category.entity.Category;
import com.basket.category.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService)
    {
        this.categoryService = categoryService;
    }

    @PostMapping
    public Category createCategory(@Valid @RequestBody Category category){
        return categoryService.createCategory(category);
    }

    @GetMapping
    public List<Category> getAllCategories(){
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Long id)
    {
        return categoryService.getCategoryById(id);
    }

    @PutMapping("/{id}")
    public Category updateCategoryById(@PathVariable Long id,@Valid @RequestBody Category category)
    {
        return categoryService.updateCategoryById(id, category);
    }

    @DeleteMapping("/{id}")
    public String deleteCategory(@PathVariable Long id)
    {
        categoryService.deleteCategory(id);
        return "Category deleted Successfully";
    }
}
