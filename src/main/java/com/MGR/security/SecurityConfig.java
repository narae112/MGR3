package com.MGR.security;

import com.MGR.service.OAuth2MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    private final OAuth2MemberService oAuth2MemberService;
    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/member/**","/admin/**").authenticated() // ~로 시작하는 uri 는 로그인 필수
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
                        .defaultSuccessUrl("/") //OAuth 로그인이 성공하면 이동할 uri 설정
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2MemberService))
                );//로그인 완료 후 회원 정보 받기

//        httpSecurity //JWT(토큰 기반)를 이용하기 때문에 session 필요 없음
//                .sessionManagement((session) -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        httpSecurity.userDetailsService(customUserDetailsService);

        return httpSecurity.build();
    }

//    public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {
//        @Override
//        public void configure(HttpSecurity http) throws Exception {
//            final AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
//            http
//                    .addFilter(new JwtAuthenticationFilter(authenticationManager, jwtUtil));
//        }
//    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
//        http.csrf(csrf -> csrf
//                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
//        //토큰 켜기
//
//        //http.csrf(AbstractHttpConfigurer::disable);
//        //토큰 끄기
//
//        http.formLogin((login) -> login
//                        .loginPage("/login")
//                        .usernameParameter("email")
//                        .passwordParameter("password")
//                        .failureUrl("/login/error")
//                        .defaultSuccessUrl("/")
//                        .loginProcessingUrl("/login"))
//                .logout((logout) -> logout
//                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//                        .logoutSuccessUrl("/")
//                        .invalidateHttpSession(true)); // 세션 삭제
//
//        http.authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/**").permitAll()
////                        .requestMatchers("/board/**").permitAll()
//                        .requestMatchers("/admin/**").permitAll()
//                        .requestMatchers("/member/**").permitAll()
////                      .requestMatchers("/admin/**").hasRole("ADMIN")
////                      .requestMatchers("/admin").hasRole("ADMIN")
//                        .anyRequest().authenticated());
//
//        http.userDetailsService(customUserDetailsService);
//
//        return http.build();
//    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
////        http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
//        // csrf 토큰 나중에 다시 생성
//        http
//                .authorizeRequests(authorize -> authorize
//                        .requestMatchers("/admin").hasRole("ADMIN")
//                        .anyRequest().permitAll()
//                )
//                .formLogin(login -> login
//                        .loginPage("/member/login")
//                        .usernameParameter("email")
//                        .passwordParameter("password")
//                        .failureUrl("/member/login/error")
//                        .defaultSuccessUrl("/")
//                        .loginProcessingUrl("/member/login")
//                )
//                .oauth2Login(oauth2Login -> oauth2Login
//                        .loginPage("/member/login")
//                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
//                                .userService(customOAuth2UserService)
//                        )
//                )

    //======== 민재씨 코드============
//    .oauth2Login(oauth2Login -> oauth2Login
//            .loginPage("/login") // 로그인 페이지 지정
//            .defaultSuccessUrl("/") // 기본 OAuth2 로그인 성공 후 이동할 페이지 설정
//                        .userInfoEndpoint(userInfo -> userInfo
//            .userService(principalOauth2UserService) // OAuth2 사용자 정보 엔드포인트 설정
//            )

//                .logout(logout -> logout
//                        .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
//                        .logoutSuccessUrl("/")
//                        .invalidateHttpSession(true)
//                )
//                .userDetailsService(customUserDetailsService);
//
//        return http.build();
//    }



//    @Bean
//    public PasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder();
//    }




//    @Bean
//    public CommandLineRunner initDb2(MemberRepository memberRepository){
//
//        return createAdmin -> {
//            boolean isAdminPresent = memberRepository.findByName("관리자2").isPresent();
//
//            if (!isAdminPresent) {
//                Member admin = new Member();
//
//                admin.setName("관리자2");
//                admin.setEmail("admin2@mgr.com");
//                admin.setNickname("초기관리자2");
//                admin.setPassword(passwordEncoder().encode("1"));
//                admin.setRole(Role.ADMIN);
//
//                memberRepository.save(admin);
//            }
//        };
//    }
//
//    @Bean
//    public CommandLineRunner initDb3(MemberRepository memberRepository){
//
//        return createAdmin -> {
//            boolean isAdminPresent = memberRepository.findByName("사용자1").isPresent();
//
//            if (!isAdminPresent) {
//                Member admin = new Member();
//
//                admin.setName("사용자1");
//                admin.setEmail("user@mgr.com");
//                admin.setNickname("초기사용자1");
//                admin.setPassword(passwordEncoder().encode("1"));
//                admin.setRole(Role.USER);
//
//                memberRepository.save(admin);
//            }
//        };
//    }
//
//    @Bean
//    public CommandLineRunner initDb4(MemberRepository memberRepository){
//
//        return createAdmin -> {
//            boolean isAdminPresent = memberRepository.findByName("사용자2").isPresent();
//
//            if (!isAdminPresent) {
//                Member admin = new Member();
//
//                admin.setName("사용자2");
//                admin.setEmail("user2@mgr.com");
//                admin.setNickname("초기사용자2");
//                admin.setPassword(passwordEncoder().encode("2"));
//                admin.setRole(Role.USER);
//
//                memberRepository.save(admin);
//            }
//        };
//    }
}