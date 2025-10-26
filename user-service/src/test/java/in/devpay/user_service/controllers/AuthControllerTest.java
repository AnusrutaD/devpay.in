package in.devpay.user_service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.devpay.user_service.dtos.LoginRequest;
import in.devpay.user_service.dtos.SignupRequest;
import in.devpay.user_service.entities.User;
import in.devpay.user_service.entities.enums.UserRole;
import in.devpay.user_service.repositories.UserRepository;
import in.devpay.user_service.utils.JWTUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JWTUtil jwtUtil;

    @Test
    public void testSignup_Success() throws Exception {
        SignupRequest request = SignupRequest.builder()
                .firstName("Test")
                .lastName("User")
                .phone("9876543210")
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
                                    "firstName": "Test",
                                    "lastName": "User"
                                    "phone": "9876543210",
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
                .firstName("Test")
                .lastName("User")
                .phone("9876543210")
                .email("test@example.com")
                .password("password123")
                .build();

        User existingUser = User.builder()
                .firstName("Test")
                .lastName("User")
                .phone("9876543210")
                .email("test@example.com")
                .password("password123")
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));

        mockMvc.perform(post("/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "firstName": "Test",
                                    "lastName": "User"
                                    "phone": "9876543210",
                                    "email": "test@example.com",
                                    "password": "password123"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User already exist"));
    }

    @Test
    public void testLogin_Success() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        User existingUser = User.builder()
                .firstName("Test")
                .lastName("User")
                .phone("9876543210")
                .email("test@example.com")
                .password("encodedPass")
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(request.getPassword(), existingUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(any(), any())).thenReturn("dummyJwtToken");

        mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"token\":\"dummyJwtToken\"}"));
    }

    @Test
    public void testLogin_UserNotFound() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("User not found"));
    }

    @Test
    public void testLogin_InvalidPassword() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("wrongPassword")
                .build();

        User existingUser = User.builder()
                .firstName("Test")
                .lastName("User")
                .phone("9876543210")
                .email("test@example.com")
                .password("encodedPass")
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(request.getPassword(), existingUser.getPassword())).thenReturn(false);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid Credential"));
    }

    @Test
    public void testLogin_Success_VerifyClaims() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        User existingUser = User.builder()
                .firstName("Test")
                .lastName("User")
                .phone("9876543210")
                .email("test@example.com")
                .password("encodedPass")
                .role(UserRole.USER)
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(request.getPassword(), existingUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(any(), any())).thenReturn("dummyJwtToken");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"token\":\"dummyJwtToken\"}"));

        ArgumentCaptor<Map<String, Object>> claimsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(jwtUtil, times(1)).generateToken(claimsCaptor.capture(), anyString());

        Map<String, Object> capturedClaims = claimsCaptor.getValue();
        assertEquals(UserRole.USER, capturedClaims.get("role"));
    }
}
