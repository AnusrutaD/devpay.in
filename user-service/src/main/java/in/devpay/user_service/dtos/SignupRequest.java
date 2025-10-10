package in.devpay.user_service.dtos;

import in.devpay.user_service.entities.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignupRequest {
    private String name;
    private String email;
    private String password;
    private String adminKey;

    public User toUser(){
        return User.builder()
                .name(this.name)
                .email(this.email)
                .password(this.password)
                .build();
    }
}
