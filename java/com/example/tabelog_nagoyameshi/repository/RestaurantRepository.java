package com.example.tabelog_nagoyameshi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.tabelog_nagoyameshi.dto.RestaurantReviewCountDto;
import com.example.tabelog_nagoyameshi.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {
	public Page<Restaurant> findByNameLike(String keyword, Pageable pageable);

	public Page<Restaurant> findByNameLikeOrAddressLike(String nameKeyword, String addressKeyword, Pageable pageable);

	public List<Restaurant> findTop10ByOrderByCreatedAtDesc();

	public List<Restaurant> findByCategoryId_Id(Integer categoryId);

	@Query("SELECT new com.example.tabelog_nagoyameshi.dto.RestaurantReviewCountDto(r.id, r.name, COUNT(rev.id), r.imageName) "
			+
			"FROM Restaurant r LEFT JOIN r.reviews rev " +
			"GROUP BY r.id")
	List<RestaurantReviewCountDto> findRestaurantsWithReviewCounts();

	@Query("SELECT new com.example.tabelog_nagoyameshi.dto.RestaurantReviewCountDto(r.id, r.name, COUNT(rev.id) AS reviewCount, r.imageName) "
			+
			"FROM Restaurant r LEFT JOIN r.reviews rev " +
			"GROUP BY r.id, r.name, r.imageName " +
			"ORDER BY reviewCount DESC")
	List<RestaurantReviewCountDto> findRestaurantsSortedByReviewCounts();

	@Query("SELECT new com.example.tabelog_nagoyameshi.dto.RestaurantReviewCountDto(r.id, r.name, COUNT(rev.id), r.imageName) "
			+
			"FROM Restaurant r LEFT JOIN r.reviews rev " +
			"GROUP BY r.id, r.name, r.imageName " +
			"ORDER BY r.createdAt DESC")
	List<RestaurantReviewCountDto> findRestaurantsNewestFirst();

}
