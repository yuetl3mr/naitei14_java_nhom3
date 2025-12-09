package org.example.framgiabookingtours.service.impl;

import lombok.extern.slf4j.Slf4j;

import org.example.framgiabookingtours.dto.request.TourRequestDTO;
import org.example.framgiabookingtours.dto.response.TourResponseDTO;
import org.example.framgiabookingtours.entity.Category;
import org.example.framgiabookingtours.entity.Tour;
import org.example.framgiabookingtours.entity.User;
import org.example.framgiabookingtours.enums.TourStatus;
import org.example.framgiabookingtours.exception.AppException;
import org.example.framgiabookingtours.exception.ErrorCode;
import org.example.framgiabookingtours.repository.CategoryRepository;
import org.example.framgiabookingtours.repository.TourRepository;
import org.example.framgiabookingtours.repository.specification.TourSpecification;
import org.example.framgiabookingtours.repository.UserRepository;
import org.example.framgiabookingtours.service.ImageUploadService;
import org.example.framgiabookingtours.service.TourService;
import org.example.framgiabookingtours.util.SecurityUtils;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TourServiceImpl implements TourService {

	private final CategoryRepository categoryRepository;
    private final TourRepository tourRepository;
    private final ImageUploadService imageUploadService;
    private final UserRepository userRepository; 

    @Override
    public Page<TourResponseDTO> getAvailableTours(int page, int size, String sortBy, String sortDirection, 
            String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice) {
        
        Sort.Direction direction = Sort.Direction.DESC;
        try {
            direction = Sort.Direction.fromString(sortDirection);
        } catch (IllegalArgumentException e) {
            
        }
        
        Specification<Tour> spec = Specification
                .where(TourSpecification.isAvailable())
                .and(TourSpecification.hasKeyword(keyword))
                .and(TourSpecification.hasCategoryId(categoryId))
                .and(TourSpecification.hasPriceRange(minPrice, maxPrice));

        Sort sort = Sort.by(direction, sortBy);
        
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Tour> tourPage = tourRepository.findAll(spec, pageable);

        return tourPage.map(this::convertToDto);
    }
    
    @Override
    public TourResponseDTO getTourDetail(Long tourId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new AppException(ErrorCode.TOUR_NOT_FOUND));

        if (tour.getStatus() != TourStatus.AVAILABLE) {
            throw new AppException(ErrorCode.TOUR_NOT_AVAILABLE);
        }
        
        return convertToDto(tour);
    }
   
    private TourResponseDTO convertToDto(Tour tour) {
        Double avgRating = 0.0; 
        
        TourResponseDTO.CategoryInfo categoryInfo = Optional.ofNullable(tour.getCategory())
                .map(c -> TourResponseDTO.CategoryInfo.builder().id(c.getId()).name(c.getName()).build())
                .orElse(null);
        
        return TourResponseDTO.builder()
                .id(tour.getId())
                .name(tour.getName())
                .location(tour.getLocation())
                .description(tour.getDescription())
                .imageUrl(tour.getImageUrl())
                .price(tour.getPrice())
                .durationDays(tour.getDurationDays())
                .availableSlots(tour.getAvailableSlots())
                .createdAt(tour.getCreatedAt())
                .updatedAt(tour.getUpdatedAt())
                .averageRating(avgRating)
                .category(categoryInfo)
                .build();
    }

    @Override
    public Optional<Tour> getTourById(Long tourId) {
        log.info("Đang lấy chi tiết tour ID: {}", tourId);
        return tourRepository.findById(tourId);
    }
   
    @Override
    public Page<Tour> getAdminTours(int page, int size, String sortBy, String sortDirection, String keyword, 
    		Long categoryId, String status, BigDecimal priceMin, BigDecimal priceMax) {
        
    	Sort.Direction direction = Sort.Direction.DESC;
        try {
            direction = Sort.Direction.fromString(sortDirection);
        } catch (IllegalArgumentException e) {
            log.warn("Sort direction không hợp lệ: {}", sortDirection);
        }

        Specification<Tour> spec = Specification
                .where(TourSpecification.hasKeyword(keyword))
                .and(TourSpecification.hasCategoryId(categoryId))
                .and(TourSpecification.hasStatus(status))
                .and(TourSpecification.hasPriceRange(priceMin, priceMax));

        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "createdAt";
        }
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return tourRepository.findAll(spec, pageable);
    }
    
    @Override
    public Tour getTourEntityById(Long tourId) {
        return tourRepository.findById(tourId)
                .orElseThrow(() -> new AppException(ErrorCode.TOUR_NOT_FOUND));
    }
    
    @Override
    @Transactional
    public Tour saveTour(TourRequestDTO request) {
        Tour tour;
        boolean isNew = (request.getId() == null);
        
        if (isNew) {
            tour = new Tour();
        } else {
            tour = getTourEntityById(request.getId()); 
        }
        
        User creator = SecurityUtils.getCurrentUser()
               .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED, "Admin chưa đăng nhập."));
        
        // giả định nếu chưa đăng nhập
        // User creator = userRepository.findById(1L) // Tìm kiếm User có ID = 1 (Giả định là Admin)
           //     .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED, "Vui lòng tạo ít nhất 1 user có ID=1 để test."));
        
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_KEY, "Category not found."));

        if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
            try {
                String baseFileName = request.getImageFile().getOriginalFilename();
                String imageUrl = imageUploadService.uploadFile(request.getImageFile(), baseFileName, "tours");
                tour.setImageUrl(imageUrl);
            } catch (IOException e) {
                throw new AppException(ErrorCode.INVALID_KEY, "Lỗi upload ảnh: " + e.getMessage());
            }
        } else if (isNew && tour.getImageUrl() == null) {
            // Yêu cầu ảnh nếu TẠO MỚI và KHÔNG có file (và tour.imageUrl đang là null)
            throw new AppException(ErrorCode.INVALID_KEY, "Ảnh tour là bắt buộc khi tạo mới.");
        } 
        
        tour.setCreator(creator);
        tour.setCategory(category);
        tour.setName(request.getName());
        tour.setDescription(request.getDescription());
        tour.setLocation(request.getLocation());
        tour.setPrice(request.getPrice());
        tour.setDurationDays(request.getDurationDays());
        tour.setAvailableSlots(request.getAvailableSlots());
        
        try {
            tour.setStatus(TourStatus.valueOf(request.getStatus().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.INVALID_KEY, "Trạng thái tour không hợp lệ.");
        }

        return tourRepository.save(tour);
    }
    
    @Override
    @Transactional
    public void deleteTour(Long tourId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new AppException(ErrorCode.TOUR_NOT_FOUND));
        
        // Logic xóa: Cần kiểm tra xem có Booking chưa hoàn thành không.
        // Tạm thời, xóa trực tiếp (Dựa trên cấu hình FK ON DELETE CASCADE)
        // Nếu FK không cho phép xóa, nó sẽ ném lỗi và được GlobalExceptionHandler xử lý
        try {
            tourRepository.delete(tour);
            log.info("Đã xóa Tour ID: {}", tourId);
        } catch (Exception e) {
            log.error("Không thể xóa Tour ID: {}", tourId, e);
            throw new AppException(ErrorCode.INVALID_KEY, "Không thể xóa tour. Vui lòng kiểm tra các ràng buộc Booking.");
        }
    }
    
    @Override
    @Transactional
    public Tour toggleStatus(Long tourId) {
        Tour tour = getTourEntityById(tourId);
        
        if (tour.getStatus() == TourStatus.AVAILABLE) {
            tour.setStatus(TourStatus.UNAVAILABLE);
        } else {
            tour.setStatus(TourStatus.AVAILABLE);
        }
        return tourRepository.save(tour);
    }
}