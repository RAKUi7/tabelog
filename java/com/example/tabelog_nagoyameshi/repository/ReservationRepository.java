package com.example.tabelog_nagoyameshi.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tabelog_nagoyameshi.entity.Reservation;
import com.example.tabelog_nagoyameshi.entity.Restaurant;
import com.example.tabelog_nagoyameshi.entity.User;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
	public Page<Reservation> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
	 List<Reservation> findByRestaurantAndReservedDatetime(Restaurant restaurant, LocalDate reservedDatetime);
}
