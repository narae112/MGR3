package com.MGR.security;

import com.MGR.entity.Member;
import com.MGR.repository.MemberRepository;
import com.MGR.service.OAuth2MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//import org.springframework.web.socket.server.standard.ServerEndpointExporter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final OAuth2MemberService oAuth2MemberService;
    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
                httpSecurity.csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));

        httpSecurity

                .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers("/ws/**").hasAnyRole("ADMIN", "USER"))
//                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/websocket/**").permitAll()
                        .requestMatchers("/api/**").hasRole( "USER")
                        .requestMatchers("/gemini/**").permitAll()
                        .requestMatchers("/js/**").permitAll()
                        .requestMatchers("/css/**").permitAll()
                        .requestMatchers("/images/**").permitAll()
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
                        )

                .logout((logout) -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)) // 세션 삭제

                .oauth2Login((oauth2login) -> oauth2login//oauth2 관련 설정
                        .loginPage("/loginForm") //로그인이 필요한데 로그인을 하지 않았다면 이동할 uri 설정
                        .defaultSuccessUrl("/ ") //OAuth 로그인이 성공하면 이동할 uri 설정
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2MemberService))
                );//로그인 완료 후 회원 정보 받기

//        httpSecurity //JWT(토큰 기반)를 이용하기 때문에 session 필요 없음
//                .sessionManagement((session) -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        httpSecurity.userDetailsService(customUserDetailsService);

        return httpSecurity.build();
    }

    public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {
        @Override
        public void configure(HttpSecurity http) throws Exception {
            final AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
            http
                    .addFilter(new JwtAuthenticationFilter(authenticationManager, jwtUtil));
        }
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



//    @Bean
//    public ServerEndpointExporter serverEndpointExporter() {
//        return new ServerEndpointExporter();
//    }
}