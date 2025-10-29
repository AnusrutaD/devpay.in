package in.devpay.user_service.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public class LoginResponse extends GetUserResponse{
    private String token;
    private List<String> roles;
}
