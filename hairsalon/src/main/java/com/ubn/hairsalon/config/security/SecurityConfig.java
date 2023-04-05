package com.ubn.hairsalon.config.security;

import com.ubn.hairsalon.config.auth.KakaoOAuth2UserService;
import com.ubn.hairsalon.config.exception.CustomAuthenticationEntryPoint;
import com.ubn.hairsalon.member.entity.Member;
import com.ubn.hairsalon.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

    @Autowired
    KakaoOAuth2UserService kakaoOAuth2UserService;

    @Autowired
    MemberRepository memberRepository;

    @Bean
    public SecurityFilterChain secufilterChain(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage("/members/login")
                .defaultSuccessUrl("/")
                .usernameParameter("email")
                .failureUrl("/members/login/error")
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
                .logoutSuccessUrl("/");

        http.oauth2Login()
                .userInfoEndpoint()
                .userService(kakaoOAuth2UserService)
                .and()
                .successHandler((request, response, authentication) -> {
                    Long kakaoId = ((DefaultOAuth2User) authentication.getPrincipal()).getAttribute("id");
                    Member member = memberRepository.findByKakaoId(kakaoId);
                    if (member == null) {
                        // 회원가입 폼으로 redirect
                        response.sendRedirect("/members/new?kakaoId=" + kakaoId);
                        return;
                    }
                    List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_" + member.getRole().toString());
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(member.getEmail(), member.getPassword(), authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    response.sendRedirect("/");
                })
                .failureHandler((request, response, exception) -> {
                    // 로그인 실패 처리 로직
                    response.sendRedirect("/members/login/kakao/error");
                })
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
                .logoutSuccessUrl("/");


        http.authorizeRequests()
                .mvcMatchers("/css/**", "/js/**", "/img/**").permitAll()
                .mvcMatchers("/", "/members/new/**", "/members/login/**", "/reserve/**", "/review/list/**", "/review/detail/**", "/images/**", "/login/**").permitAll()
                .mvcMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated();

        http.exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}