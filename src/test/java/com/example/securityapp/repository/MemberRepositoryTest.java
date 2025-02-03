package com.example.securityapp.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

import com.example.securityapp.domain.Member;
import com.example.securityapp.domain.MemberRole;

import static org.junit.jupiter.api.Assertions.*;
import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void test() {
        assertNotNull(memberRepository);
    }

    @Test
    @Rollback(false)
    public void testSave() {

        for (int i = 1; i <= 10; i++) {
            Member member = Member.builder()
                    .email("user" + i + "@gmail.com")
                    .password(passwordEncoder.encode("1111"))
                    .nickname("user" + i)
                    .build();

            member.addRoles(MemberRole.USER);

            if (i >= 5) {
                member.addRoles(MemberRole.MANAGER);
            }

            if (i >= 9) {
                member.addRoles(MemberRole.ADMIN);
            }

            memberRepository.save(member);

        }

    }

    @Test
    public void testGetWtihRoles() {

        // given
        String email = "user1@gmail.com";

        // when
        Member member = memberRepository.getWtihRoles(email);

        // then
        assertNotNull(member);

        System.out.println(member.toString());

    }

}
