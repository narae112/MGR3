package com.MGR.config;

import com.MGR.constant.Role;
import com.MGR.entity.Member;
import com.MGR.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.time.LocalDateTime;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

//protected 로 메서드 설정 주의
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
            .formLogin((login) -> login
                    .loginPage("/member/login")
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .failureUrl("/member/login/error")
                    .defaultSuccessUrl("/"))
            .logout((logout) -> logout
                    .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true)); // 세션 삭제

        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/**").permitAll()
//                        .requestMatchers("/css/**", "/js/**").permitAll()
//                        .requestMatchers("/", "/**", "/member/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated());

        return http.build();
    }

    @Bean
    protected PasswordEncoder passwordEncoder(){
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
}