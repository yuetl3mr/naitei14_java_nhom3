package org.example.framgiabookingtours.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.framgiabookingtours.dto.ApiResponse;
import org.example.framgiabookingtours.dto.CustomUserDetails;
import org.example.framgiabookingtours.dto.request.*;
import org.example.framgiabookingtours.dto.response.AuthResponseDTO;
import org.example.framgiabookingtours.entity.User;
import org.example.framgiabookingtours.service.AuthService;
import org.example.framgiabookingtours.service.CustomUserDetailsService;
import org.example.framgiabookingtours.util.JwtUtils;
import org.example.framgiabookingtours.util.SecurityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/auth")
public class AuthController {
    AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO loginDto) {
        AuthResponseDTO result = authService.login(loginDto);
        ApiResponse<AuthResponseDTO> apiResponse = ApiResponse.<AuthResponseDTO>builder()
                .code(HttpStatus.OK.value())
                .result(result)
                .message("Đăng nhập thành công!")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(
            @Valid @RequestBody RegisterRequestDTO registerDto) {
        authService.register(registerDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<Void>builder()
                .code(HttpStatus.CREATED.value())
                .message("Đăng ký thành công. Vui lòng kiểm tra email để kích hoạt.")
                .build());
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> verify(
            @Valid @RequestBody VerifyEmailRequestDTO verifyEmailRequestDTO) {
        AuthResponseDTO authResponseDTO = authService.verify(verifyEmailRequestDTO);

        ApiResponse<AuthResponseDTO> apiResponse = ApiResponse.<AuthResponseDTO>builder()
                .code(HttpStatus.OK.value())
                .result(authResponseDTO)
                .message("Xác thực thành công!")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/resend-otp")
    @Operation(summary = "Gửi lại mã OTP xác thực email")
    public ResponseEntity<ApiResponse<Void>> resendOtp(
            @Valid @RequestBody ResendOtpRequestDTO resendDto) {
        authService.resendVerificationCode(resendDto);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Mã xác thực mới đã được gửi đến email của bạn. Vui lòng kiểm tra!")
                .build());
    }

    @PostMapping("/refresh")
    @Operation(summary = "Làm mới access token bằng refresh token từ header X-Refresh-Token")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> refreshToken(
            @RequestHeader(value = "X-Refresh-Token", required = false) String refreshToken) {

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<AuthResponseDTO>builder()
                            .code(HttpStatus.BAD_REQUEST.value())
                            .message("Refresh token không được để trống. Vui lòng gửi qua header X-Refresh-Token hoặc body.")
                            .build());
        }

        RefreshTokenRequestDTO requestDTO = RefreshTokenRequestDTO.builder()
                .refreshToken(refreshToken)
                .build();

        AuthResponseDTO result = authService.refreshToken(requestDTO);

        ApiResponse<AuthResponseDTO> apiResponse = ApiResponse.<AuthResponseDTO>builder()
                .code(HttpStatus.OK.value())
                .result(result)
                .message("Làm mới token thành công!")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/logout")
    @Operation(summary = "Đăng xuất - xóa refresh token và thêm vào blacklist",
            description = "Yêu cầu Bearer token trong header Authorization")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        User user = SecurityUtils.getCurrentUser().orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<Void>builder()
                            .code(HttpStatus.UNAUTHORIZED.value())
                            .message("Người dùng chưa đăng nhập!")
                            .build());
        }
        authService.logout(authHeader);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Đăng xuất thành công!")
                .build());
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Quên mật khẩu", description = "Gửi mã xác thực để đặt lại mật khẩu")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDTO forgotPasswordRequestDTO) {
        authService.forgotPassword(forgotPasswordRequestDTO);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Mã đặt lại mật khẩu đã được gửi đến email của bạn. Vui lòng kiểm tra email.")
                .build());
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Đặt lại mật khẩu", description = "Đặt lại mật khẩu với mã xác thực")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO) {
        authService.resetPassword(resetPasswordRequestDTO);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Đặt lại mật khẩu thành công! Vui lòng đăng nhập lại.")
                .build());
    }
}
