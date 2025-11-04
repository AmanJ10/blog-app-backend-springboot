package com.aman.blog.controllers;

import com.aman.blog.domain.dtos.UserDto;
import com.aman.blog.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user")
    public ResponseEntity<UserDto> getLoggedInUser(){
        UserDto userDto = userService.getLoggedInUser();
        return ResponseEntity.ok(userDto);
    }

}
