package com.example.securityapp.security.filter;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.securityapp.dto.MemberDto;
import com.example.securityapp.util.JWTUtil;
import com.google.gson.Gson;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JWTCheckFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        String path = request.getRequestURI();

        log.info("path : {}", path);

        if (request.getMethod().equals("OPTIONS") || path.startsWith("/api/v1/members/")) {

            return true; // doFilterInternal() 호출안됨
        }

        return false; // doFilterInternal() 호출
    }

    // JWT 토큰 검증
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        log.info("authHeader : {}", authHeader); // Bearer공백JSON 문자열

        try {
            String accessToken = authHeader.substring(7);

            Map<String, Object> claims = JWTUtil.validateToken(accessToken);

            log.info("email : {}", claims.get("email"));
            log.info("password : {}", claims.get("password"));
            log.info("nickname : {}", claims.get("nickname"));
            log.info("roleNames : {}", claims.get("roleNames"));

            String email = (String) claims.get("email");
            String password = (String) claims.get("password");
            String nickname = (String) claims.get("nickname");
            @SuppressWarnings("unchecked")
            List<String> roleNames = (List<String>) claims.get("roleNames");

            MemberDto memberDto = new MemberDto(email, password, nickname, roleNames);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(memberDto,
                    memberDto.getPassword(), memberDto.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("Error : {}", e.getMessage());

            Gson gson = new Gson();

            String jsonStr = gson.toJson(Map.of("error", "ERROR_ACCESS_TOKEN"));

            response.setContentType("application/json; charset=UTF-8");

            PrintWriter pw = response.getWriter();
            pw.println(jsonStr);
            pw.close();

        }

    }

}
