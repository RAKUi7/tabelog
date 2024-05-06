package com.example.tabelog_nagoyameshi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.tabelog_nagoyameshi.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
	List<Review> findByRestaurantId(Integer restaurantId);
	
	@Query("SELECT COUNT(r) FROM Review r WHERE r.restaurant.id = :restaurantId")
    long countByRestaurantId(@Param("restaurantId") Integer restaurantId);
}
