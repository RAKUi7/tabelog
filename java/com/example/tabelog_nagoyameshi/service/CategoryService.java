package com.example.tabelog_nagoyameshi.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tabelog_nagoyameshi.entity.Category;
import com.example.tabelog_nagoyameshi.form.CategoryEditForm;
import com.example.tabelog_nagoyameshi.form.CategoryRegisterForm;
import com.example.tabelog_nagoyameshi.repository.CategoryRepository;

@Service
public class CategoryService {
	private final CategoryRepository categoryRepository;
	
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }
    
    @Transactional
    public void create(CategoryRegisterForm categoryRegisterForm) {
    	Category category = new Category();
    	category.setName(categoryRegisterForm.getName());   	
    	categoryRepository.save(category);
    }
    
    @Transactional
    public void update(CategoryEditForm categoryEditForm) {
    	Category category = categoryRepository.getReferenceById(categoryEditForm.getId());
    	category.setName(categoryEditForm.getName());
    	categoryRepository.save(category);
    }

}
