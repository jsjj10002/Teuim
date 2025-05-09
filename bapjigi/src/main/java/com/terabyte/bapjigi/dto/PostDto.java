package com.terabyte.bapjigi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    
    private Long id;
    
    @NotBlank(message = "제목은 필수 입력값입니다.")
    private String title;
    
    @NotBlank(message = "내용은 필수 입력값입니다.")
    private String content;
    
    private String imageUrl;
    
    private int viewCount;
    
    private int likeCount;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private UserInfoDto author;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfoDto {
        private Long id;
        private String username;
        private String profileName;
        private String profileImage;
    }
} 