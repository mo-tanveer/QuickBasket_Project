package com.basket.category.service;

import com.basket.category.entity.Category;
import com.basket.category.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

//tells spring :this class use and handle business logic
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    //constructor injection
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(Category category)
    {
        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories(){
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id){
        return categoryRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Category Not Found!"));
    }

    public Category updateCategoryById(Long id,Category updateCategory){

        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Category Not Found!"));

        existingCategory.setName(updateCategory.getName());
        existingCategory.setDescription((updateCategory.getDescription()));

        return categoryRepository.save(existingCategory);
    }

    public void deleteCategory(Long id)
    {
        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Category Not Found!"));

        categoryRepository.delete(category);
    }
}
