package com.example.securityapp.util;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import com.example.securityapp.exception.CustomJWTException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.InvalidClaimException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

/*
 * 1. JWT 토큰 발행 : Header, Payload(Cliam), Signature(비밀키)
 * 2. JWT 토큰 검증
 */
@Slf4j
public class JWTUtil {

    // 30자 이상
    private static String key = "1234567890123456789012345678901234567890";

    // JWT 토큰 발행
    public static String generateToken(Map<String, Object> claims, int min) { // 토큰 만료시간

        SecretKey key = null;

        try {
            // HMAC-SHA 알고리즘
            key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        String jwtStr = Jwts.builder()
                .setHeader(Map.of("typ", "JWT"))
                .setClaims(claims)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant()))
                .signWith(key)
                .compact();

        return jwtStr;

    }

    // JWT 토큰 검증
    public static Map<String, Object> validateToken(String token) {
        Map<String, Object> clamis = null;

        try {

            SecretKey key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));

            clamis = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (MalformedJwtException e) {
            throw new CustomJWTException("Malformed");
        } catch (ExpiredJwtException e) {
            throw new CustomJWTException("Expired");
        } catch (InvalidClaimException e) {
            throw new CustomJWTException("Invalid");
        } catch (JwtException e) {
            log.error("e : {}", e);
            throw new CustomJWTException("JWTError");
        } catch (Exception e) {
            throw new CustomJWTException("Error");
        }

        return clamis;
    }

}
