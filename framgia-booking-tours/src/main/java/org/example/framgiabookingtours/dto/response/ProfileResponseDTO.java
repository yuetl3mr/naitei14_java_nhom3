package org.example.framgiabookingtours.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponseDTO {
    private Long id;
    private String fullName;
    private String phone;
    private String address;
    private String avatarUrl;
    private String bankName;
    private String bankAccountNumber;
    private String email;
}