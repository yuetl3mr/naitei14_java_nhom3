package org.example.framgiabookingtours.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileUpdateRequestDTO {
	@Size(max = 100, message = "FULL_NAME_TOO_LONG")
    private String fullName;

	@Size(max = 20, message = "PHONE_TOO_LONG")
    private String phone;

    private String address;

    private String avatarUrl;

    @Size(max = 100, message = "BANK_NAME_TOO_LONG")
    private String bankName;

    @Size(max = 50, message = "BANK_ACCOUNT_TOO_LONG")
    private String bankAccountNumber;
}
