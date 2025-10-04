package backend.Backend.DTOS;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
@Data
public class RegisterRequest {
    private String email;
    private String password;
}
