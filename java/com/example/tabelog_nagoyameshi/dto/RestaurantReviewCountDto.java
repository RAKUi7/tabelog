package com.example.tabelog_nagoyameshi.dto;

import lombok.Data;

@Data
public class RestaurantReviewCountDto {
	private final Integer restaurantId;
	private final String restaurantName;
	private final Long reviewCount;
	private final String imageName;

	public RestaurantReviewCountDto(Integer restaurantId, String restaurantName, Long reviewCount, String imageName) {
		this.restaurantId = restaurantId;
		this.restaurantName = restaurantName;
		this.reviewCount = reviewCount;
		this.imageName = imageName;
	}

	public Integer getId() {
		return restaurantId;
	}
	
	public String getName() {
        return restaurantName;
    }
	
}
