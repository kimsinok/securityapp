package com.example.securityapp.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.securityapp.exception.CustomJWTException;
import com.example.securityapp.util.JWTUtil;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class ApiRefreshController {

    @GetMapping("/members/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestHeader("Authorization") String authHeader,
            @RequestParam("refreshToken") String refreshToken) {

        if (refreshToken == null) {
            throw new CustomJWTException("NULL_REFRESH");
        }

        if (authHeader == null || authHeader.length() < 7) {
            throw new CustomJWTException("INVALID_AUTH");
        }

        // 1. Accss Token 이 만료되지 않는 경우
        String accessToken = authHeader.substring(7);

        // 1. Accss Token 이 만료되지 않는 경우
        if (!checkExiredToken(accessToken)) {

            return new ResponseEntity<>(Map.of("accessToken", accessToken, "refreshToken", refreshToken),
                    HttpStatus.OK);
        }

        // 2. Accss Token 이 만료 된 경우
        Map<String, Object> claims = JWTUtil.validateToken(refreshToken);

        String newAccessToken = JWTUtil.generateToken(claims, 10);

        log.info("refreshToken IssuedAt : {}", claims.get("iat"));
        log.info("refreshToken Expiration : {}", claims.get("exp"));

        String newRefreshToken = checkTime((Integer) claims.get("exp")) == true ? JWTUtil.generateToken(claims, 60 * 24)
                : refreshToken;

        return new ResponseEntity<>(Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken),
                HttpStatus.OK);
    }

    // Access Token이 만료 여부 확인
    public boolean checkExiredToken(String accessToken) {

        try {
            JWTUtil.validateToken(accessToken);
        } catch (Exception e) {
            if (e.getMessage().equals("Expired")) {
                return true;
            }
        }
        return false;
    }

    // Refresh Token 만료 시간이 1시간 미만이지 확인
    public boolean checkTime(Integer expire) {

        // expire를 날짜(Date) 변환
        Date expDate = new Date((long) expire * 60 * 1000); // 단위 ms

        long gap = expDate.getTime() - System.currentTimeMillis(); // 단위 : ms

        long leftMin = gap / (60 * 1000); // 단위 : 분

        return leftMin < 60;

    }

}
