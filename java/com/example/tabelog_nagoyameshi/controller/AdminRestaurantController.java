package com.example.tabelog_nagoyameshi.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.tabelog_nagoyameshi.entity.Category;
import com.example.tabelog_nagoyameshi.entity.Restaurant;
import com.example.tabelog_nagoyameshi.form.RestaurantEditForm;
import com.example.tabelog_nagoyameshi.form.RestaurantRegisterForm;
import com.example.tabelog_nagoyameshi.repository.CategoryRepository;
import com.example.tabelog_nagoyameshi.repository.RestaurantRepository;
import com.example.tabelog_nagoyameshi.service.RestaurantService;

@Controller
@RequestMapping("/admin/restaurants")
public class AdminRestaurantController {
	private final RestaurantRepository restaurantRepository;
	private final RestaurantService restaurantService;
	private final CategoryRepository categoryRepository;

	public AdminRestaurantController(RestaurantRepository restaurantRepository, RestaurantService restaurantService, CategoryRepository categoryRepository) {
		this.restaurantRepository = restaurantRepository;
		this.restaurantService = restaurantService;
		this.categoryRepository = categoryRepository;
	}

	@GetMapping
	public String index(Model model,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			@RequestParam(name = "keyword", required = false) String keyword) {
		Page<Restaurant> restaurantPage;

		if (keyword != null && !keyword.isEmpty()) {
			restaurantPage = restaurantRepository.findByNameLike("%" + keyword + "%", pageable);
		} else {
			restaurantPage = restaurantRepository.findAll(pageable);
		}
		
		List<Category> categories = categoryRepository.findAll();
		Map<Integer, String> categoryMap = new HashMap<>();
		for(Category category: categories) {
			categoryMap.put(category.getId(), category.getName());
		}

		model.addAttribute("restaurantPage", restaurantPage);
		model.addAttribute("keyword", keyword);
		model.addAttribute("category", categoryMap);

		return "admin/restaurants/index";
	}

	@GetMapping("/{id}")
	public String show(@PathVariable(name = "id") Integer id, Model model) {
		Restaurant restaurant = restaurantRepository.findById(id)
		        .orElseThrow(() -> new IllegalArgumentException("Invalid restaurant ID: " + id));
		    Category category = restaurant.getCategory();

		    model.addAttribute("restaurant", restaurant);
		    model.addAttribute("category", category);

		    return "admin/restaurants/show";
	}

	@GetMapping("/register")
	public String register(Model model) {
		List<Category> categories = categoryRepository.findAll();
	    model.addAttribute("categories", categories);
		model.addAttribute("restaurantRegisterForm", new RestaurantRegisterForm());
		return "admin/restaurants/register";
	}

	@PostMapping("/create")
	public String create(@ModelAttribute @Validated RestaurantRegisterForm restaurantRegisterForm,
			BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			List<Category> categories = categoryRepository.findAll();
	        model.addAttribute("categories", categories);
			return "admin/restaurants/register";
		}
		
		Integer categoryId = restaurantRegisterForm.getCategoryId();
	    Category category = categoryRepository.findById(categoryId)
	                      .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + categoryId));

		restaurantService.create(restaurantRegisterForm, category);
		redirectAttributes.addFlashAttribute("successMessage", "レストランを登録しました。");

		return "redirect:/admin/restaurants";
	}

	@GetMapping("/{id}/edit")
	public String edit(@PathVariable(name = "id") Integer id, Model model) {
		Restaurant restaurant = restaurantRepository.getReferenceById(id);
		String imageName = restaurant.getImageName();
		RestaurantEditForm restaurantEditForm = new RestaurantEditForm(restaurant.getId(), restaurant.getName(), null,
				restaurant.getDescription(), restaurant.getLowestPrice(), restaurant.getHighestPrice(),
				restaurant.getPostalCode(), restaurant.getAddress(), restaurant.getPhoneNumber(),
				restaurant.getOpeningTime(), restaurant.getClosingTime());

		model.addAttribute("imageName", imageName);
		model.addAttribute("restaurantEditForm", restaurantEditForm);

		return "admin/restaurants/edit";
	}

	@PostMapping("/{id}/update")
	public String update(@ModelAttribute @Validated RestaurantEditForm restaurantEditForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "admin/restaurants/edit";
		}

		restaurantService.update(restaurantEditForm);
		redirectAttributes.addFlashAttribute("successMessage", "レストラン情報を編集しました。");

		return "redirect:/admin/restaurants";
	}
	
	@GetMapping("/{id}/delete")
	public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
		restaurantRepository.deleteById(id);
		
		redirectAttributes.addFlashAttribute("successMessage", "レストランを削除しました。");
		
		return "redirect:/admin/restaurants";
	}
}
