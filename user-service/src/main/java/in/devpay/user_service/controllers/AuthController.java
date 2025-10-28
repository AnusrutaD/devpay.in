package in.devpay.user_service.controllers;

import in.devpay.user_service.dtos.JWTResponse;
import in.devpay.user_service.dtos.LoginRequest;
import in.devpay.user_service.dtos.SignupRequest;
import in.devpay.user_service.entities.User;
import in.devpay.user_service.repositories.UserRepository;
import in.devpay.user_service.utils.JWTUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request){
        log.info("Received request for signup");

        Map<String, String> errors = new HashMap<>();

        if (userRepository.existsByEmail(request.getEmail())){
            log.error("Email already exist");
            errors.put("email", "Email already exists");
        }

        if (userRepository.existsByPhone(request.getPhone())){
            log.error("Phone already exist");
            errors.put("phone", "Phone already exists");
        }

        if (!errors.isEmpty()){
                log.error("Signup validation failed: {}", errors);
                return ResponseEntity.badRequest().body(errors);
        }

        request.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(request.toUser());
        log.info("User registered successfully");
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request){
        log.info("Received request for login");
        Optional<User> existingUser = userRepository.findByLogin(request.getLogin());
        if (existingUser.isEmpty()){
            log.error("User not found");
            return ResponseEntity.status(401).body("User not found");
        }

        User user = existingUser.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            log.error("Password did not matched");
            return ResponseEntity.status(401).body("Invalid Credential");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());

        String token = jwtUtil.generateToken(claims, user.getEmail());

        log.info("User logged in successfully");
        return ResponseEntity.ok(new JWTResponse(token));
    }
}
