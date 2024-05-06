package com.example.tabelog_nagoyameshi.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.tabelog_nagoyameshi.entity.Category;
import com.example.tabelog_nagoyameshi.entity.Restaurant;
import com.example.tabelog_nagoyameshi.repository.CategoryRepository;
import com.example.tabelog_nagoyameshi.service.RestaurantService;

@Controller
@RequestMapping("/categories")
public class CategoryController {
	private final CategoryRepository categoryRepository;
	private final RestaurantService restaurantService;
	
	public CategoryController(CategoryRepository categoryRepository, RestaurantService restaurantService) {
		this.categoryRepository = categoryRepository;
		this.restaurantService = restaurantService;
	}
	
	@GetMapping("/{categoryId}/restaurants")
	public String listRestaurantsByCategory(@PathVariable Integer categoryId, Model model) {
		Category selectedCategory = categoryRepository.findById(categoryId).orElse(null);
	    List<Restaurant> restaurants = restaurantService.findRestaurantsByCategory(categoryId);
	    
	    model.addAttribute("selectedCategory", selectedCategory);
	    model.addAttribute("restaurants", restaurants);
	    return "restaurants/list";
	}

}