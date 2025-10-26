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
    @GeneratedValue
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
    private UserRole role = UserRole.USER;

    @PrePersist
    public void generateUsername(){
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        if (this.username == null || this.username.isEmpty()) {
            String base = (this.firstName != null && this.firstName.isBlank()) ? this.firstName: "user";
            String sanitized = base.trim()
                    .toLowerCase(Locale.ROOT)
                    .replaceAll("[^a-z0-9]+", "");
            if (sanitized.isEmpty()) {
                sanitized = "user";
            }
            this.username = sanitized + "_" + this.id.toString().substring(0, 8);
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
