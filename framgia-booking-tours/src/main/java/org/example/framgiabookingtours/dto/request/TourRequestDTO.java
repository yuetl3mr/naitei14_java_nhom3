package org.example.framgiabookingtours.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TourRequestDTO {
    
    private Long id;
    
    @NotBlank(message = "Tên tour không được để trống")
    @Size(max = 150, message = "Tên tour quá dài (tối đa 150 ký tự)")
    private String name;
    
    @NotNull(message = "Giá tour không được để trống")
    @DecimalMin(value = "0.01", message = "Giá phải lớn hơn 0")
    private BigDecimal price;
    
    @NotNull(message = "Số ngày không được để trống")
    @Min(value = 1, message = "Tour phải có ít nhất 1 ngày")
    private Integer durationDays;

    @NotNull(message = "Số chỗ không được để trống")
    @Min(value = 0, message = "Số chỗ không hợp lệ")
    private Integer availableSlots;

    @NotBlank(message = "Địa điểm không được để trống")
    private String location;

    private String description;
    
    @NotBlank(message = "Trạng thái không được để trống")
    private String status; // (AVAILABLE/UNAVAILABLE)
    
    @NotNull(message = "Danh mục không được để trống")
    private Long categoryId;
    
    private String existingImageUrl; 
    
    // Trường ảnh (KHÔNG được Valid @NotNull, vì Update có thể không upload ảnh mới)
    private MultipartFile imageFile; 
}