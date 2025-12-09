package org.example.framgiabookingtours.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.framgiabookingtours.dto.request.CategoryRequestDTO;
import org.example.framgiabookingtours.dto.response.CategoryResponseDTO;
import org.example.framgiabookingtours.entity.Category;
import org.example.framgiabookingtours.exception.AppException;
import org.example.framgiabookingtours.exception.ErrorCode;
import org.example.framgiabookingtours.repository.CategoryRepository;
import org.example.framgiabookingtours.repository.specification.CategorySpecification;
import org.example.framgiabookingtours.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;

	@Override
	public List<CategoryResponseDTO> findAllCategories(String keyword) {
		Specification<Category> spec = Specification.where(CategorySpecification.hasKeyword(keyword));

		return categoryRepository.findAll(spec, Sort.by("id").ascending()).stream().map(this::convertToResponseDto)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public CategoryResponseDTO saveCategory(CategoryRequestDTO request) {
		Category category;

		if (request.getId() != null) {
			category = categoryRepository.findById(request.getId())
					.orElseThrow(() -> new AppException(ErrorCode.INVALID_KEY));
		} else {
			category = new Category();
		}

		if (request.getName() == null || request.getName().isBlank()) {
			throw new AppException(ErrorCode.INVALID_KEY);
		}

		category.setName(request.getName());
		category.setDescription(request.getDescription());

		Category savedCategory = categoryRepository.save(category);
		return convertToResponseDto(savedCategory);
	}

	@Override
	public CategoryResponseDTO findById(Long id) {
		Category category = categoryRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.INVALID_KEY));
		return convertToResponseDto(category);
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		categoryRepository.deleteById(id);
	}

	private CategoryResponseDTO convertToResponseDto(Category category) {
		return CategoryResponseDTO.builder().id(category.getId()).name(category.getName())
				.description(category.getDescription()).createdAt(category.getCreatedAt()).build();
	}
}