package org.example.framgiabookingtours.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.framgiabookingtours.dto.request.ProfileUpdateRequestDTO;
import org.example.framgiabookingtours.dto.response.ProfileResponseDTO;
import org.example.framgiabookingtours.entity.Profile;
import org.example.framgiabookingtours.entity.User;
import org.example.framgiabookingtours.exception.AppException;
import org.example.framgiabookingtours.exception.ErrorCode;
import org.example.framgiabookingtours.repository.ProfileRepository;
import org.example.framgiabookingtours.repository.UserRepository;
import org.example.framgiabookingtours.service.ProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    @Override
    @Transactional
    public ProfileResponseDTO updateProfile(ProfileUpdateRequestDTO request, String userEmail) {
        // 1. Lấy User từ email (giả sử đã login)
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // 2. Tìm Profile của user, nếu chưa có thì tạo mới (Lazy initialization)
        Profile profile = profileRepository.findByUserId(user.getId())
                .orElseGet(() -> Profile.builder()
                        .user(user)
                        .build());

        // 3. Map dữ liệu từ Request sang Entity
        // Chỉ update những trường có gửi lên (khác null) hoặc update toàn bộ tùy logic.
        // Update đè lên các giá trị cũ.
        if (request.getFullName() != null) profile.setFullName(request.getFullName());
        if (request.getPhone() != null) profile.setPhone(request.getPhone());
        if (request.getAddress() != null) profile.setAddress(request.getAddress());
        if (request.getAvatarUrl() != null) profile.setAvatarUrl(request.getAvatarUrl());
        if (request.getBankName() != null) profile.setBankName(request.getBankName());
        if (request.getBankAccountNumber() != null) profile.setBankAccountNumber(request.getBankAccountNumber());

        // 4. Lưu xuống DB
        Profile savedProfile = profileRepository.save(profile);

        // 5. Convert sang Response DTO
        return ProfileResponseDTO.builder()
                .id(savedProfile.getId())
                .fullName(savedProfile.getFullName())
                .phone(savedProfile.getPhone())
                .address(savedProfile.getAddress())
                .avatarUrl(savedProfile.getAvatarUrl())
                .bankName(savedProfile.getBankName())
                .bankAccountNumber(savedProfile.getBankAccountNumber())
                .email(user.getEmail())
                .build();
    }
}