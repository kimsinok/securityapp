package com.example.securityapp.service;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.securityapp.domain.Member;
import com.example.securityapp.dto.MemberDto;
import com.example.securityapp.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("username : {}", username);

        Member member = memberRepository.getWtihRoles(username);

        if (member == null) {
            throw new UsernameNotFoundException(username + " Not Found");
        }

        MemberDto mmberDto = new MemberDto(member.getEmail(),
                member.getPassword(),
                member.getNickname(),
                member.getRoles().stream().map(role -> role.name()).collect(Collectors.toList()));

        return mmberDto;
    }

}
