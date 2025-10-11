package in.devpay.user_service.controllers;

import in.devpay.user_service.configs.SecurityConfig;
import in.devpay.user_service.dtos.SignupRequest;
import in.devpay.user_service.entities.User;
import in.devpay.user_service.repositories.UserRepository;
import in.devpay.user_service.utils.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JWTUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setUp(){
        openMocks(this);
    }

    @Test
    public void testSignup_Success() throws Exception {
        SignupRequest request = SignupRequest.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());

        mockMvc.perform(post("/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Test User",
                                    "email": "test@example.com",
                                    "password": "password123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    public void testSignup_UserAlreadyExists() throws Exception {
        SignupRequest request = SignupRequest.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .build();

        User existingUser = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));

        mockMvc.perform(post("/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Test User",
                                    "email": "test@example.com",
                                    "password": "password123"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User is already exist"));
    }
}
