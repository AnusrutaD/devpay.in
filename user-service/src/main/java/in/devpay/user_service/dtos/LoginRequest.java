package in.devpay.user_service.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequest {
    private String login;
    private String password;
}
