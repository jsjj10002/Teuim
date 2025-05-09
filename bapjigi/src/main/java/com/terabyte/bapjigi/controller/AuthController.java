package com.terabyte.bapjigi.controller;

import com.terabyte.bapjigi.dto.LoginRequestDto;
import com.terabyte.bapjigi.dto.LoginResponseDto;
import com.terabyte.bapjigi.dto.UserRegisterDto;
import com.terabyte.bapjigi.model.User;
import com.terabyte.bapjigi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegisterDto userRegisterDto) {
        try {
            User user = userService.registerUser(userRegisterDto);
            return ResponseEntity.ok("사용자 등록이 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        try {
            LoginResponseDto loginResponseDto = userService.login(loginRequestDto);
            return ResponseEntity.ok(loginResponseDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("로그인에 실패했습니다: " + e.getMessage());
        }
    }

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