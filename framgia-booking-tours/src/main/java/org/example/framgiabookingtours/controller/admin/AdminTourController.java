package org.example.framgiabookingtours.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.framgiabookingtours.entity.Tour;
import org.example.framgiabookingtours.enums.TourStatus;
import org.example.framgiabookingtours.exception.AppException;
import org.example.framgiabookingtours.dto.request.TourRequestDTO;
import org.example.framgiabookingtours.dto.response.CategoryResponseDTO;
import org.example.framgiabookingtours.service.CategoryService;
import org.example.framgiabookingtours.service.TourService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin/tours")
@RequiredArgsConstructor
@Slf4j
public class AdminTourController {

    private final TourService tourService;
    private final CategoryService categoryService; 

    @GetMapping({"", "/list"})
    public String listTours(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            Model model,
            @ModelAttribute("tourRequestDTO") TourRequestDTO tourRequestDTO
    ) {
        Page<Tour> tourPage = tourService.getAdminTours(
                page, size, sortBy, sortDirection, keyword, categoryId, status, priceMin, priceMax);
        
        List<CategoryResponseDTO> categories = categoryService.findAllCategories(null);
        
        model.addAttribute("tourPage", tourPage);
        model.addAttribute("categories", categories);
        model.addAttribute("tourStatuses", TourStatus.values()); 
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedStatus", status);

        if (tourRequestDTO == null || tourRequestDTO.getName() == null) {
            model.addAttribute("tourRequestDTO", new TourRequestDTO());
        }

        return "admin/list-tours";
    }

    @PostMapping("/save")
    public String saveTour(
            @Valid @ModelAttribute("tourRequestDTO") TourRequestDTO request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            log.warn("Lỗi validation khi lưu tour. Errors: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.tourRequestDTO", bindingResult);
            redirectAttributes.addFlashAttribute("tourRequestDTO", request);
            redirectAttributes.addFlashAttribute("modalError", true);

            return "redirect:/admin/tours/list"; 
        }

        try {
            Tour savedTour = tourService.saveTour(request);
            String action = request.getId() == null ? "Tạo mới" : "Cập nhật";
            redirectAttributes.addFlashAttribute("successMessage", action + " tour **" + savedTour.getName() + "** thành công!");
        } catch (AppException e) {
            log.error("Lỗi khi lưu tour: {}", e.getErrorCode().getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getErrorCode().getMessage());
            // Lưu lại request data nếu lỗi không phải do validation
            redirectAttributes.addFlashAttribute("tourRequestDTO", request);
            redirectAttributes.addFlashAttribute("modalError", true);
        } catch (Exception e) {
            log.error("Lỗi không xác định khi lưu tour", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi không xác định khi lưu tour.");
            redirectAttributes.addFlashAttribute("tourRequestDTO", request);
            redirectAttributes.addFlashAttribute("modalError", true);
        }
        
        return "redirect:/admin/tours/list";
    }

    @GetMapping("/edit/{id}")
    public String editTour(
            @PathVariable Long id, 
            RedirectAttributes redirectAttributes
    ) {
        try {
            Tour tour = tourService.getTourEntityById(id);
            TourRequestDTO dto = mapTourToRequestDTO(tour);
            
            redirectAttributes.addFlashAttribute("tourRequestDTO", dto);
            redirectAttributes.addFlashAttribute("openEditModal", id);

        } catch (AppException e) {
            log.error("Không tìm thấy Tour ID {} để sửa: {}", id, e.getErrorCode().getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: Không tìm thấy Tour.");
        }
        return "redirect:/admin/tours/list";
    }

    @PostMapping("/delete/{id}")
    public String deleteTour(
            @PathVariable Long id, 
            RedirectAttributes redirectAttributes
    ) {
        try {
            tourService.deleteTour(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa tour ID: **" + id + "** thành công!");
        } catch (AppException e) {
            log.error("Lỗi khi xóa tour ID {}: {}", id, e.getErrorCode().getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa tour ID **" + id + "**: " + e.getErrorCode().getMessage());
        }
        return "redirect:/admin/tours/list";
    }

    @PostMapping("/toggle-status/{id}")
    public String toggleStatus(
            @PathVariable Long id, 
            RedirectAttributes redirectAttributes
    ) {
        try {
            Tour tour = tourService.toggleStatus(id);
            String newStatus = tour.getStatus().name();
            redirectAttributes.addFlashAttribute("successMessage", "Đã chuyển trạng thái tour **" + tour.getName() + "** sang **" + newStatus + "** thành công!");
        } catch (AppException e) {
            log.error("Lỗi khi chuyển trạng thái tour ID {}: {}", id, e.getErrorCode().getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getErrorCode().getMessage());
        }
        return "redirect:/admin/tours/list";
    }
    
    private TourRequestDTO mapTourToRequestDTO(Tour tour) {
        return TourRequestDTO.builder()
                .id(tour.getId())
                .name(tour.getName())
                .price(tour.getPrice())
                .durationDays(tour.getDurationDays())
                .availableSlots(tour.getAvailableSlots())
                .location(tour.getLocation())
                .description(tour.getDescription())
                .status(tour.getStatus().name())
                .categoryId(tour.getCategory() != null ? tour.getCategory().getId() : null)
                .existingImageUrl(tour.getImageUrl())
                .imageFile(null) // Luôn để null khi lấy từ DB
                .build();
    }
}