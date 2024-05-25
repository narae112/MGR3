package com.MGR.entity;

import com.MGR.dto.MemberFormDto;
import com.MGR.constant.Role;
import com.MGR.repository.MemberRepository;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Entity
@Setter @Getter @ToString
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column(unique = true)
    private String email;

    @Column
    private String password;

    @Column
    private String nickname;

    @Column
    private String birth;

    @Column
    private Boolean isSuspended;

    @Column
    private String role;

    @Column
    private String oauth2Id;

    private String provider; //공급자
    private String providerId; //공급 아이디

    @Builder
    public Member(String oauth2Id, String name, String password, String email, String role, String provider, String providerId) {
        this.oauth2Id=oauth2Id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.isSuspended = false;
    }

    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {

        Member member = new Member();

        member.setEmail(memberFormDto.getEmail());
        member.setPassword(passwordEncoder.encode(memberFormDto.getPassword()));

        member.setName(memberFormDto.getName());
        member.setNickname(memberFormDto.getNickname());
        member.setBirth(memberFormDto.getBirth());
        member.setRole("ROLE_USER");
        member.setIsSuspended(false);

        return member;
    }

    public static Member createMember(String email,String name,String password,
                                      PasswordEncoder passwordEncoder) {

        Member member = new Member();

        member.setEmail(email);
        member.setPassword(passwordEncoder.encode(password));
        member.setNickname(name);
        member.setRole("ROLE_USER");
        member.setIsSuspended(false);

        return member;
    }

    @Bean
    public CommandLineRunner initDb(MemberRepository memberRepository, PasswordEncoder passwordEncoder){

        return createAdmin -> {
            boolean isAdminPresent = memberRepository.findByName("관리자").isPresent();

            if (!isAdminPresent) {
                Member admin = new Member();

                admin.setName("관리자");
                admin.setEmail("admin@mgr.com");
                admin.setNickname("초기관리자");
                admin.setPassword(passwordEncoder.encode("1"));
                admin.setRole("ROLE_ADMIN");

                memberRepository.save(admin);
            }
        };
    }
}
