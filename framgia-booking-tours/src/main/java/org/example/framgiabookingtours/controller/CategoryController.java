package org.example.framgiabookingtours.controller;

import lombok.RequiredArgsConstructor;
import org.example.framgiabookingtours.dto.ApiResponse;
import org.example.framgiabookingtours.dto.request.CategoryRequestDTO;
import org.example.framgiabookingtours.dto.response.CategoryResponseDTO;
import org.example.framgiabookingtours.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/categories") 
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponseDTO>>> listCategories(
            @RequestParam(value = "q", required = false) String keyword) {
        
        List<CategoryResponseDTO> categories = categoryService.findAllCategories(keyword);
        
        return ResponseEntity.ok(ApiResponse.<List<CategoryResponseDTO>>builder()
                .code(1000)
                .message("Lấy danh sách danh mục thành công")
                .result(categories)
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> saveCategory(
            @Valid @RequestBody CategoryRequestDTO request) {
        
        CategoryResponseDTO savedCategory = categoryService.saveCategory(request);
        
        String message = (request.getId() == null) 
                        ? "Tạo mới danh mục thành công" 
                        : "Cập nhật danh mục ID " + savedCategory.getId() + " thành công";
                        
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<CategoryResponseDTO>builder()
            .code(1000)
            .message(message)
            .result(savedCategory)
            .build());
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDTO>> getCategoryById(@PathVariable("id") Long id) {
        CategoryResponseDTO category = categoryService.findById(id); 
        
        return ResponseEntity.ok(ApiResponse.<CategoryResponseDTO>builder()
                .code(1000)
                .message("Lấy chi tiết danh mục thành công")
                .result(category)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable("id") Long id) {
        categoryService.deleteById(id);
        
        return ResponseEntity.ok(ApiResponse.<Void>builder()
            .code(1000)
            .message("Xóa danh mục ID " + id + " thành công")
            .build());
    }
}