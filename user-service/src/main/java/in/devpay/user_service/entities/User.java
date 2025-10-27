package in.devpay.user_service.entities;

import in.devpay.user_service.dtos.GetUserResponse;
import in.devpay.user_service.entities.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Locale;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    private String lastName;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String phone;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(value = EnumType.STRING)
    private UserRole role;

    @PrePersist
    public void generateUsernameAndSetUserRole(){

        if (this.role == null){
            this.role = UserRole.USER;
        }

        if (this.username == null || this.username.isEmpty()) {
            String base = (this.firstName == null || this.firstName.isBlank()) ? "user" : this.firstName;
            String sanitized = base.trim()
                    .toLowerCase(Locale.ROOT)
                    .replaceAll("[^a-z0-9]+", "");
            if (sanitized.isEmpty()) {
                sanitized = "user";
            }
            String suffix = this.id.toString().substring(0, 8);
            this.username = sanitized + "_" + suffix;
        }
    }

    public GetUserResponse toGetResponse(){
        return GetUserResponse.builder()
                .id(this.id)
                .username(this.username)
                .firstName(this.firstName)
                .lastName(this.lastName)
                .phone(this.phone)
                .email(this.email)
                .build();
    }


}
