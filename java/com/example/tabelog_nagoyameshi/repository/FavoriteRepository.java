package com.example.tabelog_nagoyameshi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tabelog_nagoyameshi.entity.Favorite;
import com.example.tabelog_nagoyameshi.entity.Restaurant;
import com.example.tabelog_nagoyameshi.entity.User;


public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    Optional<Favorite> findByRestaurantAndUser(Restaurant restaurant, User user);
    Optional<Favorite> findByRestaurantIdAndUserId(Integer restaurantId, Integer userId);
}
