package com.terabyte.bapjigi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    
    private String token;
    private Long userId;
    private String username;
    private String profileName;
    private String profileImage;
} 