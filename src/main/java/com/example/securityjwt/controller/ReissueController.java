package com.example.securityjwt.controller;

import com.example.securityjwt.jwt.JWTUtil;
import com.example.securityjwt.service.ReissueService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final JWTUtil jwtUtil;
    private final ReissueService reissueService;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = reissueService.checkRefreshToken(request);

        if(refreshToken == null) {
            return ResponseEntity.badRequest().body("refresh token null");
        }

        //새로운 access token 생성
        String newAccessToken = reissueService.JenerateNewAccessToken(refreshToken);

        response.setHeader("access", newAccessToken);
        return ResponseEntity.ok("reissue success");
    }


}
