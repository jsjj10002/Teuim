package com.terabyte.bapjigi.dto;

import com.terabyte.bapjigi.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDto {
    
    @NotBlank(message = "아이디는 필수 입력값입니다.")
    @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하로 입력해주세요.")
    private String username;
    
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    private String password;
    
    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String name;
    
    private Integer age;
    
    private LocalDate birthDate;
    
    private User.Gender gender;
    
    private String region;
    
    private String occupation;
    
    private Integer monthlyFoodBudget;
    
    private String referralSource;
    
    private String profileName;
    
    private String profileImage;
}