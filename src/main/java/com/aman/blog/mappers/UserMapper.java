package com.aman.blog.mappers;

import com.aman.blog.domain.dtos.UserDto;
import com.aman.blog.domain.entities.User;

public class UserMapper {

    public UserMapper() {
        // prevent instantiation
    }

    public static UserDto toDto(User user) {

        if(user == null) {
            return null;
        }

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .build();
    }
}
