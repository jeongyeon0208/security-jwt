package com.example.securityjwt.controller;

import com.example.securityjwt.jwt.JWTUtil;
import com.example.securityjwt.service.RefreshService;
import com.example.securityjwt.service.ReissueService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final JWTUtil jwtUtil;
    private final ReissueService reissueService;
    private final RefreshService refreshService;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = reissueService.checkRefreshToken(request);

        if(refreshToken == null) {
            return ResponseEntity.badRequest().body("refresh token null");
        }

        boolean isExist = refreshService.checkRefreshTokenInDB(refreshToken);
        // refreshToken이 DB에 존재하지 않을 시
        if (!isExist) {
            return ResponseEntity.badRequest().body("refresh token not exist");
        }


        //새로운 access, refresh token 생성
        List<String> newTokens = reissueService.JenerateNewAccessToken(refreshToken);

        String newAccessToken = newTokens.get(0);
        String newRefreshToken = newTokens.get(1);

        refreshService.deleteAndSaveRefreshToken(refreshToken, newAccessToken);

        response.setHeader("access", newAccessToken);
        response.addCookie(reissueService.createCookie("refresh", newRefreshToken));
        return ResponseEntity.ok("reissue success");
    }


}
