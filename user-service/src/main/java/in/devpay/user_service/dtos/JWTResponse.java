package in.devpay.user_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class JWTResponse {
    private String token;
}
