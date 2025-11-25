package org.example.framgiabookingtours.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileBankUpdateRequestDTO {
	@Size(max = 100, message = "BANK_NAME_TOO_LONG")
	private String bankName;

	@Size(max = 50, message = "BANK_ACCOUNT_TOO_LONG")
	private String bankAccountNumber;
}