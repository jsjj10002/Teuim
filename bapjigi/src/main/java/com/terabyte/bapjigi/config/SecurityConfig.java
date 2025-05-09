package com.terabyte.bapjigi.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.terabyte.bapjigi.security.JwtAuthenticationFilter;
import com.terabyte.bapjigi.security.JwtTokenProvider;

/**
 * Spring Security 설정 클래스
 * 인증, 인가, 비밀번호 암호화, JWT 필터 등의 보안 설정
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    public void setJwtTokenProvider(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * JWT 인증 필터 빈 등록
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }

    /**
     * 비밀번호 암호화에 사용할 인코더 빈 등록
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 인증 관리자 빈 등록
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * CORS 설정
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 보안 필터 체인 설정
     * URL별 접근 권한, CSRF 보호, 세션 관리, JWT 필터 등을 설정
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 보호 비활성화 (JWT 사용하므로)
            .csrf(AbstractHttpConfigurer::disable)
            // CORS 설정 활성화
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // 세션 관리 전략을 STATELESS로 설정 (JWT 사용하므로 세션 불필요)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // URL별 접근 권한 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/").permitAll() // 루트 경로 접근 허용
                .requestMatchers("/api-test").permitAll() // API 테스트 페이지 접근 허용
                .requestMatchers("/error").permitAll() // 에러 페이지 접근 허용
                .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll() // 정적 리소스 접근 허용
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Swagger 문서 접근 허용
                .requestMatchers("/h2-console/**").permitAll() // H2 콘솔 접근 허용 (개발용)
                .requestMatchers("/api/auth/**").permitAll() // 인증 관련 URL은 모두 허용
                .requestMatchers("/api/posts").permitAll() // 게시물 목록 조회는 모두 허용
                .requestMatchers("/api/posts/{id}").permitAll() // 게시물 상세 조회는 모두 허용
                .requestMatchers("/api/ranking/**").permitAll() // 랭킹 조회는 모두 허용
                .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
            )
            // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // H2 콘솔 사용을 위한 설정 (개발용)
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }
} 