package com.ubn.hairsalon.config.auth;

import com.ubn.hairsalon.member.entity.Member;
import com.ubn.hairsalon.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

        @Override
        public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
            OAuth2User oAuth2User = super.loadUser(userRequest);
            Map<String, Object> attributes = oAuth2User.getAttributes();
            Long kakaoId = (Long) attributes.get("id");

            Member member = memberRepository.findByKakaoId(kakaoId);
            if (member == null) {
                throw new UsernameNotFoundException("Kakao Id not found: " + kakaoId);
            }

            List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(member.getRole().toString());
            return new DefaultOAuth2User(authorities, attributes, "id");
        }
}