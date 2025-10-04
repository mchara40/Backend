package backend.Backend.Services;

import backend.Backend.DTOS.LoginResponse;
import backend.Backend.DTOS.RegisterRequest;
import backend.Backend.Entities.UserEntity;

public interface AuthServiceInterface {
    LoginResponse attemptLogin(String email, String password);
    UserEntity registerUser(RegisterRequest request);
}
