package com.terabyte.bapjigi.security;

import java.security.Key;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

/**
 * JWT 토큰 생성, 검증 및 사용자 인증 정보 추출을 담당하는 컴포넌트
 */
@Component
public class JwtTokenProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    // application.properties에서 JWT 비밀키 가져오기
    @Value("${jwt.secret}")
    private String jwtSecret;

    // application.properties에서 JWT 토큰 만료 시간 가져오기 (밀리초)
    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;

    // JWT 서명에 사용할 키
    private Key key;

    private UserDetailsService userDetailsService;

    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * 빈 초기화 시 JWT 서명 키 생성
     */
    @PostConstruct
    public void init() {
        // HS512 알고리즘을 위한 안전한 키 생성 (512비트 이상)
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    /**
     * 사용자명으로 JWT 토큰 생성
     * @param username 사용자명
     * @return 생성된 JWT 토큰
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * JWT 토큰에서 사용자명 추출
     * @param token JWT 토큰
     * @return 사용자명
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * JWT 토큰 유효성 검증
     * @param token JWT 토큰
     * @return 유효 여부
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException ex) {
            logger.error("잘못된 JWT 토큰입니다.");
        } catch (ExpiredJwtException ex) {
            logger.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException ex) {
            logger.error("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT 토큰이 비어있습니다.");
        }
        return false;
    }

    /**
     * 토큰에서 인증 정보 추출
     * @param token JWT 토큰
     * @return 인증 객체
     */
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUsernameFromToken(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
} 