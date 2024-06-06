package com.MGR.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class GeminiRestTemplateConfig {
    //제미나이 api 에 요청을 보내기 위해 템플릿 생성

    @Bean
    public RestTemplate geminiRestTemplate() {
        RestTemplate restTemplate = new RestTemplate(); //http 요청을 보낼 때 사용하는 클라이언트 객체 생성

        restTemplate.getInterceptors().add((request, body, execution) -> execution.execute(request, body));
        //RestTemplate 에 요청 인터셉트 추가 -> 인터셉터는 요청을 가로채서 추가적인 작업을 수행/요청/응답을 수정할 수 있다

        return restTemplate; //커스텀한 restTemplate 반환 -> 인터셉트 추가버전
    }
}