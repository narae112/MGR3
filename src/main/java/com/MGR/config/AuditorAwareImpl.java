package com.MGR.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = "";
        if(authentication != null){
            userId = authentication.getName();
        }
        return Optional.of(userId);
    }
    }
//AuditorAware - 현재 사용자를 식별
//AuditorAwareImpl 클래스는 현재 사용자를 찾아서 반환
//SecurityContextHolder 사용하여 현재 인증된 사용자 이름 가져옴

// 엔티티의 생성자 및 수정자를 자동으로 설정
// 수정일도 관리