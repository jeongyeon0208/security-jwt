package com.example.securityjwt.jwt;

import com.example.securityjwt.domain.RefreshEntity;
import com.example.securityjwt.dto.CustomUserDetails;
import com.example.securityjwt.repository.RefreshRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        //클라이언트 요청에서 username과 password를 받아옴
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        //스프링 시큐리티에서 제공하는 token에 username과 password를 담음
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

        //AuthenticationManager에게 token을 전달하면 인증을 진행하고 결과를 받아옴
        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
//        단일 토큰 JWT
//        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
//
//        String username = customUserDetails.getUsername();
//        System.out.println("username = " + username);
//
//        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
//        GrantedAuthority auth = iterator.next();
//
//        String role = auth.getAuthority().toString();
//        System.out.println("role = " + role);
//
//        String token = jwtUtil.generateToken(username, role, 1000L * 60 * 10); //10분
//
//        response.addHeader("Authorization", "Bearer " + token);

        //다중 토큰 JWT

        //유저 정보
        String urername = authentication.getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority().toString();

        //토큰 생성
        String accessToken = jwtUtil.generateToken("access", urername, role, 1000L * 60 * 10); //10분
        String refreshToken = jwtUtil.generateToken("refresh", urername, role, 1000L * 60 * 60 * 24); //24시간
        saveRefreshEntity(urername, refreshToken, 1000L * 60 * 60 * 24);

        //토큰을 헤더에 추가
        response.setHeader("access", accessToken);
        response.addCookie(createCookie("refresh", refreshToken));
        response.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        response.setStatus(401);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60 * 60 * 24); //24시간
//        cookie.setSecure(true); //https 사용할 때
//        cookie.setPath("/"); //모든 경로에서 접근 가능
        cookie.setHttpOnly(true); //자바스크립트에서 접근 불가

        return cookie;
    }


    private void saveRefreshEntity(String username, String refreshToken, Long expiration) {
        Date date = new Date(System.currentTimeMillis() + expiration);
        RefreshEntity refreshEntity = RefreshEntity.builder()
                .username(username)
                .refreshToken(refreshToken)
                .expiration(date.toString())
                .build();
        refreshRepository.save(refreshEntity);
    }
}
