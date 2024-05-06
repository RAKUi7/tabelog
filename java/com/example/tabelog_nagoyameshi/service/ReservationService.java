package com.example.tabelog_nagoyameshi.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tabelog_nagoyameshi.entity.Reservation;
import com.example.tabelog_nagoyameshi.entity.Restaurant;
import com.example.tabelog_nagoyameshi.entity.User;
import com.example.tabelog_nagoyameshi.form.ReservationRegisterForm;
import com.example.tabelog_nagoyameshi.repository.ReservationRepository;
import com.example.tabelog_nagoyameshi.repository.RestaurantRepository;
import com.example.tabelog_nagoyameshi.repository.UserRepository;

@Service
public class ReservationService {
	private final ReservationRepository reservationRepository;
	private final RestaurantRepository restaurantRepository;
	private final UserRepository userRepository;

	public ReservationService(ReservationRepository reservationRepository, RestaurantRepository restaurantRepository,
			UserRepository userRepository) {
		this.reservationRepository = reservationRepository;
		this.restaurantRepository = restaurantRepository;
		this.userRepository = userRepository;
	}
	
	@Transactional
    public void create(ReservationRegisterForm reservationRegisterForm) { 
        Reservation reservation = new Reservation();
        Restaurant restaurant = restaurantRepository.getReferenceById(reservationRegisterForm.getRestaurantId());
        User user = userRepository.getReferenceById(reservationRegisterForm.getUserId());
        LocalDate reservedDatetime = LocalDate.parse(reservationRegisterForm.getReservedDatetime());
                 
                
        reservation.setRestaurant(restaurant);
        reservation.setUser(user);		
        reservation.setNumberOfPeople(reservationRegisterForm.getNumberOfPeople());
        reservation.setReservedDatetime(reservedDatetime);
        
        reservationRepository.save(reservation);
    }

}