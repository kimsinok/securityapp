package com.example.securityapp.security.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.example.securityapp.dto.MemberDto;
import com.example.securityapp.util.JWTUtil;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        log.info("authentication : {}", authentication);

        log.info("Principal : {}", authentication.getPrincipal());

        MemberDto mmberDto = (MemberDto) authentication.getPrincipal();

        Map<String, Object> claims = mmberDto.getClaims();

        String accessToken = JWTUtil.generateToken(claims, 10); // 10분

        String refreshToken = JWTUtil.generateToken(claims, 60 * 24); // 1일

        claims.put("accessToken", accessToken);

        claims.put("refreshToken", refreshToken);

        Gson gson = new Gson();

        String jsonStr = gson.toJson(claims);

        response.setContentType("application/json; charset=UTF-8");

        PrintWriter pw = response.getWriter();
        pw.println(jsonStr);
        pw.close();

    }

}
