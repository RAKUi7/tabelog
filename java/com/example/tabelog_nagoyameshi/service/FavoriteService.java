package com.example.tabelog_nagoyameshi.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.tabelog_nagoyameshi.entity.Favorite;
import com.example.tabelog_nagoyameshi.entity.Restaurant;
import com.example.tabelog_nagoyameshi.entity.User;
import com.example.tabelog_nagoyameshi.repository.FavoriteRepository;
import com.example.tabelog_nagoyameshi.repository.RestaurantRepository;
import com.example.tabelog_nagoyameshi.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class FavoriteService {

	private final FavoriteRepository favoriteRepository;
	private final UserRepository userRepository;
	private final RestaurantRepository restaurantRepository;
	
	@Autowired
    public FavoriteService(FavoriteRepository favoriteRepository, UserRepository userRepository, RestaurantRepository restaurantRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }
	
	@Transactional
	public boolean isFavorite(Integer restaurantId, Integer userId) {
	    return favoriteRepository.findByRestaurantIdAndUserId(restaurantId, userId).isPresent();
	}

	@Transactional
    public void addToFavorites(Integer restaurantId, String email) {
        User user = userRepository.findByEmail(email);
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow();
        if (favoriteRepository.findByRestaurantIdAndUserId(restaurantId, user.getId()).isEmpty()) {
            Favorite favorite = new Favorite();
            favorite.setRestaurant(restaurant);
            favorite.setUser(user);
            favoriteRepository.save(favorite);
        }
    }

    @Transactional
    public void removeFromFavorites(Integer restaurantId, String email) {
        User user = userRepository.findByEmail(email);
        Optional<Favorite> favorite = favoriteRepository.findByRestaurantIdAndUserId(restaurantId, user.getId());
        favorite.ifPresent(favoriteRepository::delete);
    }
}
