package com.MGR.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter @Setter
public class MemberFormDto {

    @NotBlank(message="이름은 필수 입력 값입니다")
    private String name;

    @NotEmpty(message = "이메일은 필수 입력 값입니다")
    @Email(message = "이메일 형식으로 입력해주세요")
    private String email; //중복 확인

    @NotEmpty(message = "비밀번호는 필수 입력 값입니다")
    @Length(min=8, max=16, message="비밀번호는 8자 이상, 16자 이하로 입력해주세요")
    private String password;

    @NotEmpty(message = "비밀번호 확인은 필수 입력 값입니다")
    @Length(min=8, max=16)
    private String password2;

    @NotEmpty(message = "별명은 필수 입력 값입니다")
    private String nickname; //중복 확인

    private String birth;

    @NotEmpty(message = "메일 인증 코드는 필수 입력 값입니다")
    @Size(min=8, max=8, message="인증코드 8자리를 입력해주세요")
    @Pattern(regexp = "^[a-zA-Z0-9]*$")
    private String code;

    private String authCode;


}
