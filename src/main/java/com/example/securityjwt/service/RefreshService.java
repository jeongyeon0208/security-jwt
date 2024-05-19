package com.example.securityjwt.service;

import com.example.securityjwt.domain.RefreshEntity;
import com.example.securityjwt.jwt.JWTUtil;
import com.example.securityjwt.repository.RefreshRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class RefreshService {

    private final RefreshRepository refreshRepository;
    private final JWTUtil jwtUtil;

    public boolean checkRefreshTokenInDB(String refreshToken) {
        return refreshRepository.existsByRefreshToken(refreshToken).orElseThrow();
    }

    public void deleteAndSaveRefreshToken(String refreshToken, String newRefreshToken) {
        String username = jwtUtil.getUserNameFromToken(refreshToken);
        refreshRepository.deleteByRefreshToken(refreshToken);

        Date date = new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24);
        RefreshEntity newRefreshEntity = RefreshEntity.builder()
                .username(username)
                .refreshToken(newRefreshToken)
                .expiration(date.toString())
                .build();

        refreshRepository.save(newRefreshEntity);
    }

}
