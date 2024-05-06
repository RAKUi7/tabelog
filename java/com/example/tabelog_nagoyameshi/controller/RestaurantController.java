package com.example.tabelog_nagoyameshi.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.tabelog_nagoyameshi.dto.RestaurantReviewCountDto;
import com.example.tabelog_nagoyameshi.entity.Category;
import com.example.tabelog_nagoyameshi.entity.Restaurant;
import com.example.tabelog_nagoyameshi.entity.Review;
import com.example.tabelog_nagoyameshi.entity.User;
import com.example.tabelog_nagoyameshi.form.ReservationInputForm;
import com.example.tabelog_nagoyameshi.repository.CategoryRepository;
import com.example.tabelog_nagoyameshi.repository.RestaurantRepository;
import com.example.tabelog_nagoyameshi.repository.ReviewRepository;
import com.example.tabelog_nagoyameshi.repository.UserRepository;
import com.example.tabelog_nagoyameshi.service.FavoriteService;
import com.example.tabelog_nagoyameshi.service.RestaurantService;

@Controller
@RequestMapping("/restaurants")
public class RestaurantController {
	private final RestaurantRepository restaurantRepository;
	private final CategoryRepository categoryRepository;
	private final ReviewRepository reviewRepository;
	private final FavoriteService favoriteService;
	private final UserRepository userRepository;
	private final RestaurantService restaurantService;

	public RestaurantController(RestaurantRepository restaurantRepository, RestaurantService restaurantService,
			CategoryRepository categoryRepository, ReviewRepository reviewRepository, FavoriteService favoriteService,
			UserRepository userRepository) {
		this.restaurantRepository = restaurantRepository;
		this.restaurantService = restaurantService;
		this.categoryRepository = categoryRepository;
		this.reviewRepository = reviewRepository;
		this.favoriteService = favoriteService;
		this.userRepository = userRepository;
	}

	@GetMapping
	public String index(@RequestParam(name = "keyword", required = false) String keyword,
			@PageableDefault(page = 0, size = 8, sort = "id", direction = Direction.ASC) Pageable pageable,
			Model model) {
		Page<Restaurant> restaurantPage;
		if (keyword != null && !keyword.isEmpty()) {
			restaurantPage = restaurantRepository.findByNameLikeOrAddressLike("%" + keyword + "%", "%" + keyword + "%",
					pageable);
		} else {
			restaurantPage = restaurantRepository.findAll(pageable);
		}

		Page<RestaurantReviewCountDto> restaurantsWithReviewCounts = restaurantService
				.getRestaurantsWithReviewCounts(restaurantPage);

		model.addAttribute("restaurantsWithReviewCounts", restaurantsWithReviewCounts);
		model.addAttribute("keyword", keyword);
		model.addAttribute("restaurantPage", restaurantPage);

		return "restaurants/index";
	}

	@GetMapping("/{id}")
	public String show(@PathVariable(name = "id") Integer id, Principal principal, Model model) {
		Restaurant restaurant = restaurantRepository.findById(id).orElse(null);

		boolean isFavorite = false;
		if (principal != null) {
			User user = userRepository.findByEmail(principal.getName());
			isFavorite = favoriteService.isFavorite(restaurant.getId(), user.getId());
		}

		Category category = categoryRepository.findById(restaurant.getCategory().getId()).orElse(null);
		List<Review> reviews = reviewRepository.findByRestaurantId(id);

		model.addAttribute("restaurant", restaurant);
		model.addAttribute("category", category);
		model.addAttribute("reviews", reviews);
		model.addAttribute("isFavorite", isFavorite);
		model.addAttribute("reservationInputForm", new ReservationInputForm());

		return "restaurants/show";
	}

	@PostMapping("/{restaurantId}/add-to-favorites")
	public String addToFavorites(@PathVariable Integer restaurantId, Principal principal,
			RedirectAttributes redirectAttributes) {
		favoriteService.addToFavorites(restaurantId, principal.getName());
		redirectAttributes.addFlashAttribute("successMessage", "お気に入りに追加されました。");
		return "redirect:/restaurants/" + restaurantId;
	}

	@PostMapping("/{restaurantId}/remove-from-favorites")
	public String removeFromFavorites(@PathVariable Integer restaurantId, Principal principal,
			RedirectAttributes redirectAttributes) {
		favoriteService.removeFromFavorites(restaurantId, principal.getName());
		redirectAttributes.addFlashAttribute("successMessage", "お気に入りから削除されました。");
		return "redirect:/restaurants/" + restaurantId;
	}

}
