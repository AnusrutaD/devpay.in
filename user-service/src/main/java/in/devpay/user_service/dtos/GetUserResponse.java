package in.devpay.user_service.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@SuperBuilder
public class GetUserResponse {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
}
