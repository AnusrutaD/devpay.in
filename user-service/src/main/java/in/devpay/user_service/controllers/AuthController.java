package in.devpay.user_service.controllers;

import in.devpay.user_service.dtos.JWTResponse;
import in.devpay.user_service.dtos.LoginRequest;
import in.devpay.user_service.dtos.SignupRequest;
import in.devpay.user_service.entities.User;
import in.devpay.user_service.repositories.UserRepository;
import in.devpay.user_service.utils.JWTUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request){
        log.info("Received request for signup");
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()){
            log.error("User is already exist");
            return ResponseEntity.badRequest().body("User is already exist");
        }

        request.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(request.toUser());
        log.info("User registered successfully");
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        log.info("Received request for login");
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
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
