package com.finalproject.bttd.apicontroller;

import com.finalproject.bttd.dto.AuthResponseDto;
import com.finalproject.bttd.dto.LoginDto;
import com.finalproject.bttd.dto.UserDto;
import com.finalproject.bttd.entity.User;
import com.finalproject.bttd.security.JwtUtils;
import com.finalproject.bttd.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ApiUserController {
    @Autowired
    private UserService userService;
//    @Autowired
//    private AuthenticationManager authenticationManager;
//    @Autowired
//    private JWTGenerator jwtGenerator;

    private final Long expiredMs = 1000 * 60 * 60L;

    @PostMapping("/api/user")
    public ResponseEntity<String> createUser(@RequestBody UserDto userDto) {

        User created = userService.create(userDto);
        return new ResponseEntity<>("{\"data\":{\"success\":true}}", HttpStatus.OK);
    }



    @PostMapping("/api/login")
    public String login(@RequestBody LoginDto loginDto){
//      Authentication authentication = authenticationManager.authenticate(
//              new UsernamePasswordAuthenticationToken(loginDto.getUser_name(), loginDto.getUser_password()));
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        String token = jwtGenerator.generateToken(authentication);
//        System.out.println(token);
//        return new ResponseEntity<>(new AuthResponseDto(token), HttpStatus.OK);


        return JwtUtils.createJwt(loginDto.getUser_name(),"mysecret.ee.ff.gg", expiredMs);

    }


//
}
