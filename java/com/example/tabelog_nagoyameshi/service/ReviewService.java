package com.example.tabelog_nagoyameshi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.tabelog_nagoyameshi.entity.Review;
import com.example.tabelog_nagoyameshi.repository.ReviewRepository;

@Service
public class ReviewService {
	@Autowired
    private ReviewRepository reviewRepository;

    public List<Review> getReviewsByRestaurantId(Integer restaurantId) {
        return reviewRepository.findByRestaurantId(restaurantId);
    }

    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }
}
