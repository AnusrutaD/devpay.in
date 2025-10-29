package in.devpay.user_service.controllers;

import in.devpay.user_service.dtos.LoginRequest;
import in.devpay.user_service.dtos.LoginResponse;
import in.devpay.user_service.dtos.SignupRequest;
import in.devpay.user_service.entities.User;
import in.devpay.user_service.services.interfaces.UserService;
import in.devpay.user_service.utils.DevpayUserDetails;
import in.devpay.user_service.utils.JWTUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request){
        log.info("Received request for signup");

        Map<String, String> errors = new HashMap<>();

        if (userService.isEmailExist(request.getEmail())){
            log.error("Email already exist");
            errors.put("email", "Email already exists");
        }

        if (userService.isPhoneExist(request.getPhone())){
            log.error("Phone already exist");
            errors.put("phone", "Phone already exists");
        }

        if (!errors.isEmpty()){
                log.error("Signup validation failed: {}", errors);
                return ResponseEntity.badRequest().body(errors);
        }

        request.setPassword(passwordEncoder.encode(request.getPassword()));
        userService.createUser(request.toUser());
        log.info("User registered successfully");
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request){
        log.info("Received request for login");

        Map<String, Object> errorResponse = new HashMap<>();

        Optional<User> existingUser = userService.getUserByLogin(request.getLogin());

        if (existingUser.isEmpty()){
            log.error("User not found");
            errorResponse.put("message", "Username or email or phone is not exist");
            errorResponse.put("status", false);
            return new ResponseEntity<Object>(errorResponse, HttpStatus.NOT_FOUND);
        }

        User user = existingUser.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            errorResponse.put("message", "Wrong password");
            errorResponse.put("status", false);
            return new ResponseEntity<Object>(errorResponse, HttpStatus.NOT_FOUND);
        }

        DevpayUserDetails userDetails = new DevpayUserDetails(user);

        String jwtToken = jwtUtil.generateTokenFromUsername(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        LoginResponse response = user.toLoginResponse(jwtToken, roles);

        return ResponseEntity.ok(response);
    }
}
