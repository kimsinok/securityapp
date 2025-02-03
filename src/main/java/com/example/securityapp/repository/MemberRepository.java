package com.example.securityapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.securityapp.domain.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {

    // 이메일에 해당하는 사용자 정보를 조회하다.
    @Query("SELECT m FROM Member AS m JOIN FETCH m.roles WHERE m.email = :email")
    Member getWtihRoles(@Param("email") String email);

}
