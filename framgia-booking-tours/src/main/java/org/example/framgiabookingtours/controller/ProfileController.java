package org.example.framgiabookingtours.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.example.framgiabookingtours.dto.ApiResponse;
import org.example.framgiabookingtours.dto.request.ProfileBankUpdateRequestDTO;
import org.example.framgiabookingtours.dto.request.ProfileUpdateRequestDTO;
import org.example.framgiabookingtours.dto.response.ProfileResponseDTO;
import org.example.framgiabookingtours.exception.AppException;
import org.example.framgiabookingtours.exception.ErrorCode;
import org.example.framgiabookingtours.service.ImageUploadService;
import org.example.framgiabookingtours.service.ProfileService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

	private final ProfileService profileService;
	private final ImageUploadService imageUploadService;

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

	@PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponse<ProfileResponseDTO> uploadAndSetAvatar(@RequestParam("file") MultipartFile file,
			@RequestHeader(value = "X-User-Email", defaultValue = "test@gmail.com") String userEmail) {
		if (file == null || file.isEmpty()) {
			throw new AppException(ErrorCode.FILE_NULL);
		}

		try {
			String fileName = "avatar-" + UUID.randomUUID().toString();
			String folder = "user_avatars";

			String avatarUrl = imageUploadService.uploadFile(file, fileName, folder);

			ProfileUpdateRequestDTO request = new ProfileUpdateRequestDTO();
			request.setAvatarUrl(avatarUrl);

			ProfileResponseDTO result = profileService.updateProfile(request, userEmail);

			return ApiResponse.<ProfileResponseDTO>builder().result(result).message("Avatar updated successfully")
					.build();

		} catch (AppException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException(ErrorCode.UPLOAD_FAILED);
		}
	}

	@PutMapping("/banking")
	public ApiResponse<ProfileResponseDTO> updateBankingInfo(
			@RequestBody @Valid ProfileBankUpdateRequestDTO bankRequest,
			@RequestHeader(value = "X-User-Email", defaultValue = "test@gmail.com") String userEmail) {
		ProfileUpdateRequestDTO fullRequest = new ProfileUpdateRequestDTO();

		fullRequest.setBankName(bankRequest.getBankName());
		fullRequest.setBankAccountNumber(bankRequest.getBankAccountNumber());

		ProfileResponseDTO result = profileService.updateProfile(fullRequest, userEmail);

		return ApiResponse.<ProfileResponseDTO>builder().result(result).message("Update banking info success").build();
	}

}