package com.example.securityjwt.service;

import com.example.securityjwt.domain.User;
import com.example.securityjwt.dto.CustomUserDetails;
import com.example.securityjwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user != null) {
            return new CustomUserDetails(user);
        }
        else {
            return null;
//            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
    }
}
