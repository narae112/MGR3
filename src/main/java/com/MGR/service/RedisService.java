package com.MGR.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void saveRefreshToken(Long userId, String refreshToken) {
        redisTemplate.opsForValue().set("refreshToken:" + userId, refreshToken);
        System.out.println("redis 에 저장 saveRefreshToken = " + refreshToken);
    }

    public String getRefreshToken(Long userId) {
        return (String) redisTemplate.opsForValue().get("refreshToken:" + userId);
    }

    public void deleteRefreshToken(Long userId) {
        redisTemplate.delete("refreshToken:" + userId);
    }
}
