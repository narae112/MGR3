package com.MGR.entity;

import com.MGR.dto.MemberFormDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;


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

    private String profileImgUrl;

    private String provider; //공급자
    private String providerId; //공급 아이디

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MemberCoupon> memberCoupons = new HashSet<>();
    
    @Builder
    public Member(String oauth2Id, String name, String nickname, String password, String email, String role, String provider, String providerId, String profileImgUrl) {
        this.oauth2Id=oauth2Id;
        this.name = name;
        this.nickname = nickname;
        this.password = password;
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.profileImgUrl = profileImgUrl;
        this.isSuspended = false;
    }

    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {

        Member member = new Member();

        member.setEmail(memberFormDto.getEmail());
        member.setPassword(passwordEncoder.encode(memberFormDto.getPassword()));

        member.setName(memberFormDto.getName());
        member.setNickname(memberFormDto.getNickname());
        member.setProfileImgUrl("/img/login/profile.png");
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
}
