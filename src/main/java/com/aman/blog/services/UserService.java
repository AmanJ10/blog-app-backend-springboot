package com.aman.blog.services;

import com.aman.blog.domain.dtos.RegisterRequest;
import com.aman.blog.domain.dtos.UserDto;

public interface UserService {

    UserDto createUser(RegisterRequest registerRequest);

    UserDto getLoggedInUser();
}
