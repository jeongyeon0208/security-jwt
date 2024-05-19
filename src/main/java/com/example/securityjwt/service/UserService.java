package com.example.securityjwt.service;

import com.example.securityjwt.domain.User;
import com.example.securityjwt.dto.JoinDto;
import com.example.securityjwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void join(JoinDto joinDto) {
        Boolean exists = userRepository.existsByUsername(joinDto.getUsername());
        if(exists) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }

        User user = User.builder()
                .username(joinDto.getUsername())
                .password(bCryptPasswordEncoder.encode(joinDto.getPassword()))
                .role("ROLE_USER")
                .build();
        userRepository.save(user);
    }
}
