package in.devpay.user_service.controllers;

import in.devpay.user_service.dtos.CreateUserRequest;
import in.devpay.user_service.dtos.GetUserResponse;
import in.devpay.user_service.entities.User;
import in.devpay.user_service.services.interfaces.UserService;
import lombok.AllArgsConstructor;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/users/")
public class UserController {

    private UserService service;

    @PostMapping
    public ResponseEntity<GetUserResponse> createUser(@RequestBody CreateUserRequest request){
        log.info("Requested for create new user");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createUser(request.toUser()).toGetResponse());
    }

    @GetMapping("/{username}")
    public ResponseEntity<GetUserResponse> getUserById(@PathVariable String username){
        log.info("Requested for get user with username: " + username);
        return service.getUserByUsername(username).map(value ->
                ResponseEntity.status(HttpStatus.OK)
                        .body(value.toGetResponse()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }



    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all/")
    public ResponseEntity<List<GetUserResponse>> getAllUsers(){
        log.info("Requested for get all users");
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.getAllUsers().stream().map(User::toGetResponse).toList());
    }
}
