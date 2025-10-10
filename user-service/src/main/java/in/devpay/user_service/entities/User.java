package in.devpay.user_service.entities;

import in.devpay.user_service.dtos.GetUserResponse;
import in.devpay.user_service.entities.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(value = EnumType.STRING)
    private UserRole role = UserRole.USER;

    public GetUserResponse toGetResponse(){
        return GetUserResponse.builder()
                .id(this.id)
                .name(this.name)
                .email(this.email)
                .build();
    }


}
