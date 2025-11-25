package org.example.framgiabookingtours.service;

import org.example.framgiabookingtours.dto.request.ProfileUpdateRequestDTO;
import org.example.framgiabookingtours.dto.response.ProfileResponseDTO;

public interface ProfileService {
    ProfileResponseDTO updateProfile(ProfileUpdateRequestDTO request, String userEmail);
}