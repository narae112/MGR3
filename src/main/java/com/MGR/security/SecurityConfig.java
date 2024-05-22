package com.MGR.security;

import com.MGR.constant.Role;
import com.MGR.entity.Member;
import com.MGR.repository.MemberRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
//    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
        //토큰 켜기

        //http.csrf(AbstractHttpConfigurer::disable);
        //토큰 끄기

        http.formLogin((login) -> login
                        .loginPage("/member/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .failureUrl("/member/login/error")
                        .defaultSuccessUrl("/")
                        .loginProcessingUrl("/member/login"))
                .logout((logout) -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)); // 세션 삭제

        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/**").permitAll()
//                        .requestMatchers("/board/**").permitAll()
                        .requestMatchers("/admin/**").permitAll()
                        .requestMatchers("/member/**").permitAll()
//                      .requestMatchers("/admin/**").hasRole("ADMIN")
//                      .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated());

        http.userDetailsService(customUserDetailsService);

        return http.build();
    }

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



    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner initDb(MemberRepository memberRepository){

        return createAdmin -> {
            boolean isAdminPresent = memberRepository.findByName("관리자").isPresent();

            if (!isAdminPresent) {
                Member admin = new Member();

                admin.setName("관리자");
                admin.setEmail("admin@mgr.com");
                admin.setNickname("초기관리자");
                admin.setPassword(passwordEncoder().encode("1"));
                admin.setRole(Role.ADMIN);

                memberRepository.save(admin);
            }
        };
    }

    @Bean
    public CommandLineRunner initDb2(MemberRepository memberRepository){

        return createAdmin -> {
            boolean isAdminPresent = memberRepository.findByName("관리자2").isPresent();

            if (!isAdminPresent) {
                Member admin = new Member();

                admin.setName("관리자2");
                admin.setEmail("admin2@mgr.com");
                admin.setNickname("초기관리자2");
                admin.setPassword(passwordEncoder().encode("1"));
                admin.setRole(Role.ADMIN);

                memberRepository.save(admin);
            }
        };
    }

    @Bean
    public CommandLineRunner initDb3(MemberRepository memberRepository){

        return createAdmin -> {
            boolean isAdminPresent = memberRepository.findByName("사용자1").isPresent();

            if (!isAdminPresent) {
                Member admin = new Member();

                admin.setName("사용자1");
                admin.setEmail("user@mgr.com");
                admin.setNickname("초기사용자1");
                admin.setPassword(passwordEncoder().encode("1"));
                admin.setRole(Role.USER);

                memberRepository.save(admin);
            }
        };
    }

    @Bean
    public CommandLineRunner initDb4(MemberRepository memberRepository){

        return createAdmin -> {
            boolean isAdminPresent = memberRepository.findByName("사용자2").isPresent();

            if (!isAdminPresent) {
                Member admin = new Member();

                admin.setName("사용자2");
                admin.setEmail("user2@mgr.com");
                admin.setNickname("초기사용자2");
                admin.setPassword(passwordEncoder().encode("2"));
                admin.setRole(Role.USER);

                memberRepository.save(admin);
            }
        };
    }
}