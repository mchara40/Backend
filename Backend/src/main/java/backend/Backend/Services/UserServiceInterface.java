package backend.Backend.Services;

import backend.Backend.Entities.UserEntity;

import java.util.Optional;

public interface UserServiceInterface {
    Optional<UserEntity> findByEmail(String email);
    UserEntity createUser(String email, String rawPassword);
    boolean existsByEmail(String email);
}
