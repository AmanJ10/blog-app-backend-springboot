package com.aman.blog.services.impl;

import com.aman.blog.domain.dtos.RegisterRequest;
import com.aman.blog.domain.dtos.UserDto;
import com.aman.blog.domain.entities.User;
import com.aman.blog.exceptions.UserAlreadyExistsException;
import com.aman.blog.mappers.UserMapper;
import com.aman.blog.repositories.UserRepository;
import com.aman.blog.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto createUser(RegisterRequest registerRequest) {

        // Check if User email already exists
        if(userRepository.existsByEmailIgnoreCase(registerRequest.getEmail())){
                throw new UserAlreadyExistsException("User already exists with email: "
                        + registerRequest.getEmail());
        }
        // Check if Username already exists
        if (userRepository.existsByUsernameIgnoreCase(registerRequest.getUsername())) {
            throw new UserAlreadyExistsException("User already exists with username: " + registerRequest.getUsername());
        }

        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(encodedPassword)
                .build();

        User saved = userRepository.save(user);

        return UserDto.builder()
                .id(saved.getId())
                .username(saved.getUsername())
                .email(saved.getEmail())
                .build();
    }

    @Override
    public UserDto getLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(principal instanceof UserDetails userDetails) {
            String email = userDetails.getUsername();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
            return UserMapper.toDto(user);
        }else {
            throw new RuntimeException("No authenticated user found");
        }
    }
}
