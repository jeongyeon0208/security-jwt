package com.example.securityjwt.service;

import com.example.securityjwt.jwt.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReissueService {
    private final JWTUtil jwtUtil;

    public String checkRefreshToken(HttpServletRequest request) {

        String refreshToken = null;

        Cookie cookies[] = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refreshToken = cookie.getValue();
            }
        }

        //refresh token이 없을 시
        if (refreshToken == null) {
            new Exception("refresh token null");
        }


        //refresh token이 만료됐을 시
        if(jwtUtil.isExpired(refreshToken)) {
            new Exception("refresh token expired");
        }


        //refresh token의 category check
        String category = jwtUtil.getCategoryFromToken(refreshToken);
        if(!category.equals("refresh")) {
            new Exception("invalid refresh token");
        }
        return refreshToken;
    }

    public List<String> JenerateNewAccessToken(String refreshToken) {
        String username = jwtUtil.getUserNameFromToken(refreshToken);
        String role = jwtUtil.getRoleFromToken(refreshToken);

        List<String> tokens = new ArrayList<>();

        //make new Access, Refresh Token
        String newAccessToken = jwtUtil.generateToken("access", username, role, 1000L * 60 * 10); //10분
        String newRefreshToken = jwtUtil.generateToken("refresh", username, role, 1000L * 60 * 60 * 24); //24시간

        tokens.add(newAccessToken);
        tokens.add(newRefreshToken);

        return tokens;
    }


    public Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60 * 60 * 24); //24시간
//        cookie.setSecure(true); //https 사용할 때
//        cookie.setPath("/"); //모든 경로에서 접근 가능
        cookie.setHttpOnly(true); //자바스크립트에서 접근 불가

        return cookie;
    }
}
