package com.example.tabelog_nagoyameshi.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.tabelog_nagoyameshi.entity.Restaurant;
import com.example.tabelog_nagoyameshi.entity.Review;
import com.example.tabelog_nagoyameshi.entity.User;
import com.example.tabelog_nagoyameshi.repository.RestaurantRepository;
import com.example.tabelog_nagoyameshi.repository.ReviewRepository;
import com.example.tabelog_nagoyameshi.repository.UserRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/reviews")
public class ReviewController {
    private ReviewRepository reviewRepository;
    private RestaurantRepository restaurantRepository;
    private UserRepository userRepository;

    public ReviewController(ReviewRepository reviewRepository, RestaurantRepository restaurantRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/add")
    public String addReview(@Valid @ModelAttribute("review") Review review, BindingResult result,
                            @RequestParam("restaurantId") Integer restaurantId,
                            @RequestParam("imageFile") MultipartFile imageFile,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "reviewForm";
        }
        
        if (!imageFile.isEmpty()) {
	        String imageName = imageFile.getOriginalFilename();
	        String hashedImageName = generateNewFileName(imageName);
	        Path filePath = Paths.get("src/main/resources/static/storage/", hashedImageName);
	        copyImageFile(imageFile, filePath);
	        review.setImageName(hashedImageName);
	    }

        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElse(null);
        if (restaurant == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Restaurant not found.");
            return "redirect:/";
        }
        review.setRestaurant(restaurant);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(email);

        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "ユーザーが見つかりませんでした。");
            return "redirect:/";
        }
        review.setUser(user);

        reviewRepository.save(review);
        redirectAttributes.addFlashAttribute("successMessage", "レビューを投稿しました。");
        return "redirect:/";
    }

    public String generateNewFileName(String fileName) {
        String[] fileNames = fileName.split("\\.");                
        for (int i = 0; i < fileNames.length - 1; i++) {
            fileNames[i] = UUID.randomUUID().toString();            
        }
        String hashedFileName = String.join(".", fileNames);
        return hashedFileName;
    }
    
    public void copyImageFile(MultipartFile imageFile, Path filePath) {           
        try {
            Files.copy(imageFile.getInputStream(), filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }          
    }

}
