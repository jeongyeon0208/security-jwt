package com.example.securityjwt.service;

import com.example.securityjwt.jwt.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public String JenerateNewAccessToken(String refreshToken) {
        String username = jwtUtil.getUserNameFromToken(refreshToken);
        String role = jwtUtil.getRoleFromToken(refreshToken);

        //make new Access Token
        String newAccessToken = jwtUtil.generateToken("access", username, role, 1000L * 60 * 10); //10분

        return newAccessToken;
    }
}
