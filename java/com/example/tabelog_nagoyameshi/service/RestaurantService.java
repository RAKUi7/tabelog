package com.example.tabelog_nagoyameshi.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.tabelog_nagoyameshi.dto.RestaurantReviewCountDto;
import com.example.tabelog_nagoyameshi.entity.Category;
import com.example.tabelog_nagoyameshi.entity.Restaurant;
import com.example.tabelog_nagoyameshi.form.RestaurantEditForm;
import com.example.tabelog_nagoyameshi.form.RestaurantRegisterForm;
import com.example.tabelog_nagoyameshi.repository.RestaurantRepository;
import com.example.tabelog_nagoyameshi.repository.ReviewRepository;

@Service
public class RestaurantService {
	private final RestaurantRepository restaurantRepository;
	private final ReviewRepository reviewRepository;
	
	public RestaurantService(RestaurantRepository restaurantRepository, ReviewRepository reviewRepository) {
		this.restaurantRepository = restaurantRepository;
		this.reviewRepository = reviewRepository;
	}
	
	public List<RestaurantReviewCountDto> getRestaurantsWithReviewCounts() {
        return restaurantRepository.findRestaurantsWithReviewCounts();
    }

	@Transactional
	public void create(RestaurantRegisterForm restaurantRegisterForm, Category category) {
		Restaurant restaurant = new Restaurant();
		MultipartFile imageFile = restaurantRegisterForm.getImageFile();
		
		if (!imageFile.isEmpty()) {
            String imageName = imageFile.getOriginalFilename(); 
            String hashedImageName = generateNewFileName(imageName);
            Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
            copyImageFile(imageFile, filePath);
            restaurant.setImageName(hashedImageName);
        }

		restaurant.setName(restaurantRegisterForm.getName());
		restaurant.setDescription(restaurantRegisterForm.getDescription());
		restaurant.setLowestPrice(restaurantRegisterForm.getLowestPrice());
		restaurant.setHighestPrice(restaurantRegisterForm.getHighestPrice());
		restaurant.setPostalCode(restaurantRegisterForm.getPostalCode());
		restaurant.setAddress(restaurantRegisterForm.getAddress());
		restaurant.setPhoneNumber(restaurantRegisterForm.getPhoneNumber());
		restaurant.setOpeningTime(restaurantRegisterForm.getOpeningTime());
		restaurant.setClosingTime(restaurantRegisterForm.getClosingTime());
		restaurant.setCategory(category);

		restaurantRepository.save(restaurant);
	}

	@Transactional
	public void update(RestaurantEditForm restaurantEditForm) {
		Restaurant restaurant = restaurantRepository.getReferenceById(restaurantEditForm.getId());
		MultipartFile imageFile = restaurantEditForm.getImageFile();

		if (!imageFile.isEmpty()) {
	        String imageName = imageFile.getOriginalFilename();
	        String hashedImageName = generateNewFileName(imageName);
	        Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
	        copyImageFile(imageFile, filePath);
	        restaurant.setImageName(hashedImageName);
	    }
		

		restaurant.setName(restaurantEditForm.getName());
		restaurant.setDescription(restaurantEditForm.getDescription());
		restaurant.setLowestPrice(restaurantEditForm.getLowestPrice());
		restaurant.setHighestPrice(restaurantEditForm.getHighestPrice());
		restaurant.setPostalCode(restaurantEditForm.getPostalCode());
		restaurant.setAddress(restaurantEditForm.getAddress());
		restaurant.setPhoneNumber(restaurantEditForm.getPhoneNumber());
		restaurant.setOpeningTime(restaurantEditForm.getOpeningTime());
		restaurant.setClosingTime(restaurantEditForm.getClosingTime());
		
		restaurantRepository.save(restaurant);
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

	public List<Restaurant> findRestaurantsByCategory(Integer categoryId) {
		return restaurantRepository.findByCategoryId_Id(categoryId);
	}
	
	public Page<RestaurantReviewCountDto> getRestaurantsWithReviewCounts(Page<Restaurant> restaurantPage) {
	    List<RestaurantReviewCountDto> dtoList = restaurantPage.getContent().stream()
	        .map(restaurant -> new RestaurantReviewCountDto(
	                restaurant.getId(),
	                restaurant.getName(),
	                reviewRepository.countByRestaurantId(restaurant.getId()),
	                restaurant.getImageName()))
	        .collect(Collectors.toList());

	    return new PageImpl<>(dtoList, restaurantPage.getPageable(), restaurantPage.getTotalElements());
	}
	
	public Page<RestaurantReviewCountDto> getRestaurantsSortedBy(String sortBy, Pageable pageable) {
        List<RestaurantReviewCountDto> restaurants;
        if ("reviews".equals(sortBy)) {
            restaurants = restaurantRepository.findRestaurantsSortedByReviewCounts();
        } else {
            restaurants = restaurantRepository.findRestaurantsNewestFirst();
        }
        
        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), restaurants.size());
        List<RestaurantReviewCountDto> pageContent = restaurants.subList(start, end);

        return new PageImpl<>(pageContent, pageable, restaurants.size());
    }
}
