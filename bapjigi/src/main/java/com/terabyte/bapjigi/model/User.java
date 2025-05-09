package com.terabyte.bapjigi.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

/**
 * 사용자 정보를 저장하는 엔티티 클래스
 * Spring Security의 UserDetails 인터페이스를 구현하여 인증에 사용
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자 아이디 (중복 불가)
    @Column(nullable = false, unique = true)
    private String username;

    // 암호화된 비밀번호
    @Column(nullable = false)
    private String password;

    // 사용자 이름
    @Column(nullable = false)
    private String name;

    // 사용자 나이
    private Integer age;

    // 생년월일
    private LocalDate birthDate;

    // 성별 (MALE, FEMALE, OTHER)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    // 거주 지역
    private String region;

    // 직업
    private String occupation;

    // 월 식비 예산
    private Integer monthlyFoodBudget;

    // 서비스 유입 경로
    private String referralSource;

    // 프로필 이름 (중복 불가)
    @Column(nullable = false, unique = true)
    private String profileName;

    // 프로필 이미지 경로
    private String profileImage;

    // 계정 활성화 상태
    @Column(nullable = false)
    private boolean enabled = true;

    // 사용자 역할 (기본값: ROLE_USER)
    @Column(nullable = false)
    private String role = "ROLE_USER";

    // 생성 시간
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 수정 시간
    private LocalDateTime updatedAt;

    /**
     * 엔티티 생성 시 자동으로 현재 시간을 설정
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * 엔티티 수정 시 자동으로 현재 시간을 설정
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * 사용자의 권한 목록 반환
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    /**
     * 계정 만료 여부 (true: 만료되지 않음)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정 잠금 여부 (true: 잠기지 않음)
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 인증 정보 만료 여부 (true: 만료되지 않음)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 계정 활성화 여부 (true: 활성화됨)
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 성별 열거형
     */
    public enum Gender {
        MALE, FEMALE, OTHER
    }
} 