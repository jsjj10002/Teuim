package com.terabyte.bapjigi.service;

import com.terabyte.bapjigi.dto.LoginRequestDto;
import com.terabyte.bapjigi.dto.LoginResponseDto;
import com.terabyte.bapjigi.dto.UserRegisterDto;
import com.terabyte.bapjigi.model.User;
import com.terabyte.bapjigi.repository.UserRepository;
import com.terabyte.bapjigi.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
    }

    @Transactional
    public User registerUser(UserRegisterDto registerDto) {
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new RuntimeException("이미 사용 중인 아이디입니다.");
        }

        String profileName = generateUniqueProfileName(registerDto.getName());

        User user = User.builder()
                .username(registerDto.getUsername())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .name(registerDto.getName())
                .age(registerDto.getAge())
                .birthDate(registerDto.getBirthDate())
                .gender(registerDto.getGender())
                .region(registerDto.getRegion())
                .occupation(registerDto.getOccupation())
                .monthlyFoodBudget(registerDto.getMonthlyFoodBudget())
                .referralSource(registerDto.getReferralSource())
                .profileName(profileName)
                .profileImage(registerDto.getProfileImage() != null ? registerDto.getProfileImage() : "default.png")
                .enabled(true)
                .role("ROLE_USER")
                .build();

        return userRepository.save(user);
    }

    private String generateUniqueProfileName(String name) {
        String baseName = name != null && !name.isEmpty() ? name : "user";
        String profileName = baseName + "_" + UUID.randomUUID().toString().substring(0, 8);
        
        while (userRepository.existsByProfileName(profileName)) {
            profileName = baseName + "_" + UUID.randomUUID().toString().substring(0, 8);
        }
        
        return profileName;
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getUsername(),
                        loginRequestDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(loginRequestDto.getUsername());
        
        User user = userRepository.findByUsername(loginRequestDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return LoginResponseDto.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .profileName(user.getProfileName())
                .profileImage(user.getProfileImage())
                .build();
    }

    @Transactional
    public User changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    @Transactional
    public User updateProfile(String username, String profileName, String profileImage) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if (profileName != null && !profileName.equals(user.getProfileName())) {
            if (userRepository.existsByProfileName(profileName)) {
                throw new RuntimeException("이미 사용 중인 프로필 이름입니다.");
            }
            user.setProfileName(profileName);
        }

        if (profileImage != null) {
            user.setProfileImage(profileImage);
        }

        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("인증 정보를 찾을 수 없습니다.");
        }
        
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }
} 