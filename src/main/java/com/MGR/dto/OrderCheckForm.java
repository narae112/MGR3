package com.MGR.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
@Getter
@Setter
public class OrderCheckForm {
    @NotBlank
    @Length(min=1, message = "주문 번호를 입력해주세요")
    private String orderNum;
}
