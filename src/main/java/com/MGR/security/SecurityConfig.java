package com.MGR.security;

import com.MGR.entity.Member;
import com.MGR.oauth2.OAuth2SuccessHandler;
import com.MGR.repository.MemberRepository;
import com.MGR.service.OAuth2MemberService;
import com.MGR.service.PrincipalDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final PrincipalDetailsService principalDetailsService;
    private final OAuth2MemberService oAuth2MemberService;
    private final JwtProvider jwtProvider;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, AuthenticationManager authenticationManager) throws Exception {

        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/gemini/**").permitAll()
                        .requestMatchers("/js/**").permitAll()
                        .requestMatchers("/css/**").permitAll()
                        .requestMatchers("/img/**").permitAll()
                        .requestMatchers("/admin/**").authenticated() // ~로 시작하는 uri 는 로그인 필수
                        .requestMatchers("/admin/**").hasRole("ADMIN") //admin 으로 시작하는 uri 는 관리자 계정만 접근 가능
                        .anyRequest().permitAll())//나머지 uri 는 모든 접근 허용

                .formLogin((login) -> login // form login 관련 설정
                        .loginPage("/login")
                        .usernameParameter("email") // Member 가 username 이라는 파라미터 갖고 있으면 안 적어도 됨.
                        .passwordParameter("password")
                        .loginProcessingUrl("/login") // 로그인 요청 받는 url
                        .defaultSuccessUrl("/") // 로그인 성공 후 이동할 url
                        .failureUrl("/login/error")
                        .failureHandler(customAuthenticationFailureHandler)
                )

                .logout((logout) -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true) // 세션 삭제
                        .deleteCookies("JSESSIONID", "at")) // 쿠키도 삭제

                .oauth2Login((oauth2login) -> oauth2login//oauth2 관련 설정
                        .loginPage("/loginForm") //로그인이 필요한데 로그인을 하지 않았다면 이동할 uri 설정
                        .successHandler(oAuth2SuccessHandler)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2MemberService))
                )

                .addFilterBefore(new JwtAuthenticationFilter(authenticationManager, jwtProvider, customAuthenticationFailureHandler),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthorizationFilter(authenticationManager, jwtProvider),
                        JwtAuthenticationFilter.class);

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
            AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
            authBuilder.userDetailsService(principalDetailsService);
        return authBuilder.build();
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

//    @Bean
//    public CommandLineRunner initDbUser(MemberRepository memberRepository, PasswordEncoder passwordEncoder){
//
//        return createAdmin -> {
//            boolean isAdminPresent = memberRepository.findByName("사용자").isPresent();
//
//            if (!isAdminPresent) {
//                Member user = new Member();
//
//                user.setName("");
//                user.setEmail("user@mgr.com");
//                user.setNickname("지구123");
//                user.setBirth("2023-05-31");
//                user.setPassword(passwordEncoder.encode("1"));
//                user.setRole("ROLE_USER");
//
//                memberRepository.save(user);
//            }
//        };
//    }
}