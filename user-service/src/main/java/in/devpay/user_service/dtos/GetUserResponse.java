package in.devpay.user_service.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GetUserResponse {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
}
