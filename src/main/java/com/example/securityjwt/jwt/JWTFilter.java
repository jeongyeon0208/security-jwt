package com.example.securityjwt.jwt;

import com.example.securityjwt.domain.User;
import com.example.securityjwt.dto.CustomUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    //다중 토큰 JWTFilter
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //request 헤더 access 키에 있는 토큰을 받아옴
        String accessToken = request.getHeader("access");

        // 토큰이 없을 시 필터체인을 통과시킴
        if(accessToken == null) {
            System.out.println("token = " + accessToken);
            filterChain.doFilter(request, response);

            //조건이 해당되면 메소드 종료
            return;
        }

        //토큰이 만료되었을 시 다음필터로 넘기지 않음
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            PrintWriter writer = response.getWriter();
            writer.println("access token expired");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String category = jwtUtil.getCategoryFromToken(accessToken);

        if(!category.equals("access")) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        //토큰에서 username과 role을 추출
        String username = jwtUtil.getUserNameFromToken(accessToken);
        String role = jwtUtil.getRoleFromToken(accessToken);

        User user = User.builder()
                .username(username)
                .role(role)
                .password("tempPassword")
                .build();

        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        //일시적인 세션 생성
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    //단일 토큰 JWTFilter
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        //request에서 헤더에 있는 토큰을 받아옴
//        String authorization = request.getHeader("Authorization");
//
//        // 토큰이 없거나 Bearer로 시작하지 않으면 필터체인을 통과시킴
//        if(authorization == null || !authorization.startsWith("Bearer ")) {
//            System.out.println("token = " + authorization);
//            filterChain.doFilter(request, response);
//
//            //조건이 해당되면 메소드 종료
//            return;
//        }
//
//        String token = authorization.split(" ")[1];
//
//        //토큰 소멸시간 검증
//        if(jwtUtil.isExpired(token)) {
//            System.out.println("토큰 만료");
//            filterChain.doFilter(request, response);
//
//            return;
//        }
//
//        String username = jwtUtil.getUserNameFromToken(token);
//        String role = jwtUtil.getRoleFromToken(token);
//
//        User user = User.builder()
//                .username(username)
//                .role(role)
//                .password("tempPassword")
//                .build();
//
//        CustomUserDetails customUserDetails = new CustomUserDetails(user);
//
//        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
//
//        SecurityContextHolder.getContext().setAuthentication(authToken);
//
//        filterChain.doFilter(request, response);
//    }
}
