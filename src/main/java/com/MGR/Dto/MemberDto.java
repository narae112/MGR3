package com.MGR.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberDto {

    private String name;

    private String email; //중복 확인

    private String password1;

    private String password2;

    private String nickname; //중복 확인

    private String birth;

}
