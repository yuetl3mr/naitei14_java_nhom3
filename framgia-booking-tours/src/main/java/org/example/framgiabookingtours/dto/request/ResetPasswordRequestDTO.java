package org.example.framgiabookingtours.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequestDTO {
    @NotBlank(message = "EMAIL_IS_REQUIRED")
    @Email(message = "INVALID_EMAIL")
    private String email;

    @NotBlank(message = "RESET_PASSWORD_CODE_IS_REQUIRED")
    @Pattern(regexp = "^\\d{6}$", message = "RESET_PASSWORD_CODE_INVALID")
    private String code;

    @NotBlank(message = "PASSWORD_IS_REQUIRED")
    @Size(min = 8, message = "INVALID_PASSWORD")
    private String newPassword;
}
