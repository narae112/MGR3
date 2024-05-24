//package com.MGR.entity;
//
//import com.MGR.constant.Role;
//import com.MGR.dto.MemberFormDto;
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//@Entity
//@Getter @Setter
//public class KaKaoMember {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column
//    private String name;
//
//    @Column(unique = true)
//    private String email;
//
//    @Column
//    private String password;
//
//    @Column
//    private String nickname;
//
//    @Column
//    private String birth;
//
//    @Column
//    private Boolean isSuspended;
//
//    @Enumerated(EnumType.STRING)
//    private Role role;
//
//    public static KaKaoMember createKaKaoMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {
//
//        KaKaoMember member = new KaKaoMember();
//
//        member.setEmail(memberFormDto.getEmail());
//        member.setPassword(passwordEncoder.encode(memberFormDto.getPassword()));
//
//        member.setName(memberFormDto.getName());
//        member.setNickname(memberFormDto.getNickname());
//        member.setBirth(memberFormDto.getBirth());
//        member.setRole(Role.USER);
//        member.setIsSuspended(false);
//
//        return member;
//    }
//}
