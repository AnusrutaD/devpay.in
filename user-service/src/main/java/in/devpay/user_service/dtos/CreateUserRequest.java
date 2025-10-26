package in.devpay.user_service.dtos;

import in.devpay.user_service.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    private String lastName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number must be 10 digits starting with 6,7,8, or 9")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @NotBlank(message = "Password is required")
    private String password;


    public User toUser(){
        return User.builder()
                .firstName(this.firstName)
                .lastName(this.lastName)
                .phone(this.phone)
                .email(this.email)
                .password(this.password)
                .build();
    }
}
