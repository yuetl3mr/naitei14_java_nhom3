package org.example.framgiabookingtours.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.framgiabookingtours.dto.ApiResponse;
import org.example.framgiabookingtours.dto.request.ProfileUpdateRequestDTO;
import org.example.framgiabookingtours.dto.response.ProfileResponseDTO;
import org.example.framgiabookingtours.service.ProfileService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

	private final ProfileService profileService;

	@PutMapping
	public ApiResponse<ProfileResponseDTO> updateMyProfile(@RequestBody @Valid ProfileUpdateRequestDTO request,
			// TẠM THỜI: Lấy email từ Header để test khi chưa có Security
			// Sau này có Security thì dùng @AuthenticationPrincipal hoặc
			// SecurityContextHolder
			@RequestHeader(value = "X-User-Email", defaultValue = "test@gmail.com") String userEmail

	// Lấy email từ Security Context (User đang đăng nhập)
	// var authentication = SecurityContextHolder.getContext().getAuthentication();
	// String userEmail = authentication.getName();
	) {
		ProfileResponseDTO result = profileService.updateProfile(request, userEmail);

		return ApiResponse.<ProfileResponseDTO>builder().result(result).message("Update profile success").build();
	}
}