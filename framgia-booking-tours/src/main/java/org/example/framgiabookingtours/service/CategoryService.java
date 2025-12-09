package org.example.framgiabookingtours.service;

import java.util.List;

import org.example.framgiabookingtours.dto.request.CategoryRequestDTO;
import org.example.framgiabookingtours.dto.response.CategoryResponseDTO;

public interface CategoryService {
	
	List<CategoryResponseDTO> findAllCategories(String keyword);

	CategoryResponseDTO saveCategory(CategoryRequestDTO request);

	CategoryResponseDTO findById(Long id);

    void deleteById(Long id);
}