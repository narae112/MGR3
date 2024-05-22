package com.MGR.entity;

import com.MGR.dto.MemberFormDto;
import com.MGR.constant.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Setter @Getter @ToString
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

    @Enumerated(EnumType.STRING)
    private Role role;

    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {

        Member member = new Member();

        member.setEmail(memberFormDto.getEmail());
        member.setPassword(passwordEncoder.encode(memberFormDto.getPassword()));

        member.setName(memberFormDto.getName());
        member.setNickname(memberFormDto.getNickname());
        member.setBirth(memberFormDto.getBirth());
        member.setRole(Role.USER);
        member.setIsSuspended(false);

        return member;
    }

    public static Member createMember(String email,String name,String password,
                                      PasswordEncoder passwordEncoder) {

        Member member = new Member();

        member.setEmail(email);
        member.setPassword(passwordEncoder.encode(password));
        member.setNickname(name);
        member.setRole(Role.USER);
        member.setIsSuspended(false);

        return member;
    }
}
