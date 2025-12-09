package org.example.framgiabookingtours.repository.specification;

import org.example.framgiabookingtours.entity.Tour;
import org.example.framgiabookingtours.enums.TourStatus;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TourSpecification {

    public static Specification<Tour> isAvailable() {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("status"), TourStatus.AVAILABLE);
    }
    
    public static Specification<Tour> hasKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        String pattern = "%" + keyword.toLowerCase().trim() + "%";
        
        return (root, query, criteriaBuilder) -> {
            Predicate nameLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern);
            Predicate locationLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("location")), pattern);
            Predicate descriptionLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern);
            
            return criteriaBuilder.or(nameLike, locationLike, descriptionLike);
        };
    }
    
    public static Specification<Tour> hasCategoryId(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Tour> hasPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) >= 0) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) > 0) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            
            if (predicates.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    public static Specification<Tour> hasStatus(String statusParam) {
        return (root, query, criteriaBuilder) -> {
            if (statusParam == null 
                || statusParam.isBlank() 
                || statusParam.equalsIgnoreCase("null")) {

                return criteriaBuilder.conjunction(); // Không filter theo status
            }

            try {
                TourStatus tourStatus = TourStatus.valueOf(statusParam.toUpperCase());
                return criteriaBuilder.equal(root.get("status"), tourStatus);
            } catch (IllegalArgumentException e) {
                // Status không hợp lệ → không trả về dữ liệu
                return criteriaBuilder.disjunction();
            }
        };
    }

}