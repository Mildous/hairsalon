package com.ubn.hairsalon.member.entity;

import com.ubn.hairsalon.config.entity.BaseEntity;
import com.ubn.hairsalon.member.constant.OAuth2Provider;
import com.ubn.hairsalon.member.constant.Gender;
import com.ubn.hairsalon.member.constant.Role;
import com.ubn.hairsalon.member.dto.MemberFormDto;
import com.ubn.hairsalon.reserve.entity.Reserve;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name="member")
@Data @EqualsAndHashCode(callSuper=true)
public class Member extends BaseEntity implements UserDetails {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String phone;

    private LocalDate birth;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "member", cascade = CascadeType.PERSIST)
    private List<Reserve> reserves;

    // Only admin
    private String memo;

    @Enumerated(EnumType.STRING)
    private OAuth2Provider oAuth2Provider;

    private Long kakaoId;


    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {
        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setPhone(memberFormDto.getPhone());
        member.setBirth(memberFormDto.getBirth());
        member.setGender(memberFormDto.getGender());
        String password = passwordEncoder.encode(memberFormDto.getPassword());
        member.setPassword(password);
        member.setRole(Role.USER);
        member.setOAuth2Provider(memberFormDto.getOAuth2Provider());
        member.setKakaoId(memberFormDto.getKakaoId());
        return member;
    }

    public void updateMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {
        this.phone = memberFormDto.getPhone();
        this.birth = memberFormDto.getBirth();
        this.gender = memberFormDto.getGender();
        String password = passwordEncoder.encode(memberFormDto.getPassword());
        this.password = password;
    }

    public void withdraw(String reason) {
        this.phone = null;
        this.birth = null;
        this.password = null;
        this.kakaoId = null;
        this.role = Role.WITHDRAW;

        // Remove personal data from all reserve entities
        for (Reserve reserve : reserves) {
            reserve.removePersonalData();
        }
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(getRole().name()));
        return authorities;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
