package org.example.framgiabookingtours.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.example.framgiabookingtours.dto.request.TourRequestDTO;
import org.example.framgiabookingtours.dto.response.TourResponseDTO;
import org.example.framgiabookingtours.entity.Tour;
import org.springframework.data.domain.Page;

public interface TourService {

	Page<TourResponseDTO> getAvailableTours(int page, int size, String sortBy, String sortDirection, 
            String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice);
	
	TourResponseDTO getTourDetail(Long tourId);

	Optional<Tour> getTourById(Long tourId);
	
	Page<Tour> getAdminTours(int page, int size, String sortBy, String sortDirection, String keyword, 
			Long categoryId, String status, BigDecimal priceMin, BigDecimal priceMax);

	Tour getTourEntityById(Long tourId); 

    Tour saveTour(TourRequestDTO request); 
    
    void deleteTour(Long tourId);
    
    Tour toggleStatus(Long tourId); 
    
}