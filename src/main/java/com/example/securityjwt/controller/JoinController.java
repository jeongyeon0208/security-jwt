package com.example.securityjwt.controller;

import com.example.securityjwt.dto.JoinDto;
import com.example.securityjwt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JoinController {

    private final UserService userService;

    @PostMapping("/join")
    public String joinProcess(@RequestBody JoinDto joinDto) {
        userService.join(joinDto);
        return "join Success!";
    }
}
