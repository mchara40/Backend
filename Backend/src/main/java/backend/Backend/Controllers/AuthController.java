package backend.Backend.Controllers;

import backend.Backend.DTOS.LoginRequest;
import backend.Backend.DTOS.LoginResponse;
import backend.Backend.DTOS.RegisterRequest;
import backend.Backend.Entities.UserEntity;
import backend.Backend.Security.JwtIssuer;
import backend.Backend.Security.UserPrincipal;
import backend.Backend.Services.AuthServiceInterface;
import backend.Backend.Services.impl.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthServiceInterface authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        var loginResponse = authService.attemptLogin(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<UserEntity> register(@RequestBody RegisterRequest request) {
        var user = authService.registerUser(request);
        return ResponseEntity.ok(user);
    }
}