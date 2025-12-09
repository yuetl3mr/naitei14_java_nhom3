package org.example.framgiabookingtours.controller.admin;

import lombok.RequiredArgsConstructor;
import org.example.framgiabookingtours.dto.request.CategoryRequestDTO;
import org.example.framgiabookingtours.dto.response.CategoryResponseDTO;
import org.example.framgiabookingtours.exception.AppException;
import org.example.framgiabookingtours.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import java.util.List;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public String listCategories(Model model, 
                                 @ModelAttribute("categoryForm") CategoryRequestDTO categoryRequest, 
                                 @RequestParam(value = "q", required = false) String keyword) {

    	List<CategoryResponseDTO> categories = categoryService.findAllCategories(keyword);

        model.addAttribute("categories", categories);
        model.addAttribute("categoryForm", categoryRequest);
        model.addAttribute("currentKeyword", keyword);
        model.addAttribute("pageTitle", "Quản lý Danh mục");
        
        return "admin/categories"; 
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            CategoryResponseDTO categoryDto = categoryService.findById(id);
            
            CategoryRequestDTO editRequest = CategoryRequestDTO.builder()
                    .id(categoryDto.getId())
                    .name(categoryDto.getName())
                    .description(categoryDto.getDescription())
                    .build();
            
            ra.addFlashAttribute("categoryForm", editRequest);
            ra.addFlashAttribute("showEditModal", true);
            
            return "redirect:/admin/categories";
        } catch (AppException e) {
            ra.addFlashAttribute("errorMessage", "Không tìm thấy Danh mục.");
            return "redirect:/admin/categories";
        }
    }

    @PostMapping("/save")
    public String saveCategory(@ModelAttribute("categoryForm") @Valid CategoryRequestDTO categoryRequest, // @Valid cho Thymeleaf
                               RedirectAttributes ra) {
        try {
            CategoryResponseDTO savedCategory = categoryService.saveCategory(categoryRequest);
            String message = (categoryRequest.getId() == null) 
                            ? "Tạo mới danh mục thành công!" 
                            : "Cập nhật danh mục ID " + savedCategory.getId() + " thành công!";
            ra.addFlashAttribute("successMessage", message);
        } catch (AppException e) {
            ra.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            ra.addFlashAttribute("categoryForm", categoryRequest); 
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id, RedirectAttributes ra) {
        try {
            categoryService.deleteById(id);
            ra.addFlashAttribute("successMessage", "Xóa danh mục ID " + id + " thành công.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Không thể xóa danh mục ID " + id + " vì đang được sử dụng bởi Tour.");
        }
        return "redirect:/admin/categories";
    }
}