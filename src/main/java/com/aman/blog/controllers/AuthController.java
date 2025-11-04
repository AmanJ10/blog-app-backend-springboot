package com.aman.blog.controllers;

import com.aman.blog.domain.dtos.AuthResponse;
import com.aman.blog.domain.dtos.LoginRequest;
import com.aman.blog.domain.dtos.RegisterRequest;
import com.aman.blog.domain.dtos.UserDto;
import com.aman.blog.services.AuthenticationService;
import com.aman.blog.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        UserDetails user = authenticationService.authenticate(
                loginRequest.getEmail(), loginRequest.getPassword()
        );

        String token = authenticationService.generateToken(user);
        long expiresInSeconds = 86_400L;

        // Set token in an HTTP-only cookie
        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(true) // set to true when using HTTPS
                .sameSite("None")
                .path("/")
                .maxAge(expiresInSeconds)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .expiresIn(expiresInSeconds) // 24 hours in second
                .build();

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest registerRequest) {
            UserDto userDto = userService.createUser(registerRequest);
            return ResponseEntity.ok(userDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
        return ResponseEntity.noContent().build();
    }

}
