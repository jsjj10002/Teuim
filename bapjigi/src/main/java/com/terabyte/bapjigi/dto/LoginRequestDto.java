package com.terabyte.bapjigi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {
    
    @NotBlank(message = "아이디는 필수 입력값입니다.")
    private String username;
    
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;
} 