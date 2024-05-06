package com.example.tabelog_nagoyameshi.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.tabelog_nagoyameshi.dto.RestaurantReviewCountDto;
import com.example.tabelog_nagoyameshi.entity.Category;
import com.example.tabelog_nagoyameshi.entity.Restaurant;
import com.example.tabelog_nagoyameshi.service.CategoryService;
import com.example.tabelog_nagoyameshi.service.RestaurantService;

@Controller
public class HomeController {
	private final CategoryService categoryService;
    private final RestaurantService restaurantService;
	
    public HomeController(CategoryService categoryService, RestaurantService restaurantService) {
        this.categoryService = categoryService;
        this.restaurantService = restaurantService;
    }
   
    @GetMapping("/")
    public String index(@RequestParam(name = "sort", defaultValue = "newest") String sort, Model model, Pageable pageable) {
        Pageable pageableWithSize = PageRequest.of(pageable.getPageNumber(), 8, pageable.getSort());
        
        Page<RestaurantReviewCountDto> restaurantsPage = restaurantService.getRestaurantsSortedBy(sort, pageableWithSize);

        List<Category> categories = categoryService.findAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("restaurantsPage", restaurantsPage);
        model.addAttribute("currentPage", pageable.getPageNumber());
        model.addAttribute("totalPages", restaurantsPage.getTotalPages());
        model.addAttribute("sort", sort);
        return "index";
    }

	@PostMapping("/")
	public String filterRestaurantsByCategory(@RequestParam("categoryId") Integer categoryId, Model model) {
		List<Restaurant> restaurants = restaurantService.findRestaurantsByCategory(categoryId);
		model.addAttribute("categories", categoryService.findAllCategories());
		model.addAttribute("restaurants", restaurants);
		return "index";
	}
}
