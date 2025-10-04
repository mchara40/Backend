package backend.Backend.Services.impl;

import backend.Backend.DTOS.LoginResponse;
import backend.Backend.DTOS.RegisterRequest;
import backend.Backend.Entities.UserEntity;
import backend.Backend.Security.JwtIssuer;
import backend.Backend.Security.UserPrincipal;
import backend.Backend.Services.AuthServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService  implements AuthServiceInterface {
    private final JwtIssuer jwtIssuer;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @Override
    public LoginResponse attemptLogin(String email, String password) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        var principal = (UserPrincipal) authentication.getPrincipal();

        var token = jwtIssuer.issue(JwtIssuer.Request.builder()
                .userId(principal.getUserId())
                .email(principal.getEmail())
                .roles(Collections.emptyList()) // No roles, empty list
                .build());

        return LoginResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public UserEntity registerUser(RegisterRequest request) {
        if (userService.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        return userService.createUser(request.getEmail(), request.getPassword());
    }
}

