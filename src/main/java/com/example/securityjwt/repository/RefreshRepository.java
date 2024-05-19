package com.example.securityjwt.repository;

import com.example.securityjwt.domain.RefreshEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshRepository extends JpaRepository<RefreshEntity, Long> {
    Optional<Boolean> existsByRefreshToken(String RefreshToken);

    @Transactional
    void deleteByRefreshToken(String refreshToken);
}
