package org.example.framgiabookingtours.repository.specification;

import org.example.framgiabookingtours.entity.Category;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import jakarta.persistence.criteria.Predicate;

public class CategorySpecification {
    public static Specification<Category> hasKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(keyword)) {
                return null;
            }
            String pattern = "%" + keyword.toLowerCase().trim() + "%";
            
            Predicate nameLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern);
            Predicate descLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern);
            
            return criteriaBuilder.or(nameLike, descLike);
        };
    }
}