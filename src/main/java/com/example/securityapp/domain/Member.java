package com.example.securityapp.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_member")
@ToString
public class Member {
    @Id
    private String email;

    private String password;

    private String nickname;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "member_role", joinColumns = @JoinColumn(name = "email", referencedColumnName = "email"))
    @Enumerated(EnumType.STRING)
    private List<MemberRole> roles = new ArrayList<>();

    // 비즈니스 메소드

    // 권한 추가
    public void addRoles(MemberRole role) {
        if (!roles.contains(role)) {
            roles.add(role);
        }
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

}
