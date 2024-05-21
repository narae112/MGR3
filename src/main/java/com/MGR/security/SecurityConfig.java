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
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
        // csrf 토큰 나중에 다시 생성
        //http.csrf(AbstractHttpConfigurer::disable);

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
//                        .requestMatchers("/admin/**").hasRole("ADMIN")

//                .requestMatchers("/admin").hasRole("ADMIN")

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
                admin.setPassword(passwordEncoder().encode("admin"));
                admin.setRole(Role.ADMIN);

                memberRepository.save(admin);
            }
        };
    }

//    @Bean
//    public CommandLineRunner initDbUser(MemberRepository memberRepository){
//
//        return createAdmin -> {
//            boolean isAdminPresent = memberRepository.findByName("사용자").isPresent();
//
//            if (!isAdminPresent) {
//                Member user = new Member();
//
//                user.setName("사용자");
//                user.setEmail("mrg2024@kakao.com");
//                user.setNickname("초기사용자");
//                user.setPassword(passwordEncoder().encode("USER"));
//                user.setRole(Role.USER);
//
//                memberRepository.save(user);
//            }
//        };
//    }
}