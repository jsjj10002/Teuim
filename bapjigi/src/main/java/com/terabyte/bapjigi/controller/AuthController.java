package com.terabyte.bapjigi.controller;

import com.terabyte.bapjigi.dto.LoginRequestDto;
import com.terabyte.bapjigi.dto.LoginResponseDto;
import com.terabyte.bapjigi.dto.UserRegisterDto;
import com.terabyte.bapjigi.model.User;
import com.terabyte.bapjigi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 관련 API를 처리하는 컨트롤러
 * 회원가입, 로그인, 비밀번호 변경, 프로필 수정 등의 엔드포인트 제공
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 회원 가입 처리
     * @param userRegisterDto 회원 가입 정보
     * @return 회원 가입 성공 메시지
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegisterDto userRegisterDto) {
        try {
            User user = userService.registerUser(userRegisterDto);
            return ResponseEntity.ok("사용자 등록이 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 로그인 처리
     * @param loginRequestDto 로그인 요청 정보
     * @return JWT 토큰 및 사용자 정보
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        try {
            LoginResponseDto loginResponseDto = userService.login(loginRequestDto);
            return ResponseEntity.ok(loginResponseDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("로그인에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 비밀번호 변경
     * @param oldPassword 현재 비밀번호
     * @param newPassword 새 비밀번호
     * @return 비밀번호 변경 성공 메시지
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestParam String oldPassword, 
                                           @RequestParam String newPassword) {
        try {
            User user = userService.getCurrentUser();
            userService.changePassword(user.getUsername(), oldPassword, newPassword);
            return ResponseEntity.ok("비밀번호가 변경되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 프로필 정보 업데이트
     * @param profileName 프로필 이름 (선택적)
     * @param profileImage 프로필 이미지 (선택적)
     * @return 프로필 업데이트 성공 메시지
     */
    @PostMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestParam(required = false) String profileName,
                                           @RequestParam(required = false) String profileImage) {
        try {
            User user = userService.getCurrentUser();
            userService.updateProfile(user.getUsername(), profileName, profileImage);
            return ResponseEntity.ok("프로필이 업데이트되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 