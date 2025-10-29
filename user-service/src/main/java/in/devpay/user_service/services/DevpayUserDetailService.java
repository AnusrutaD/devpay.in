package in.devpay.user_service.services;

import in.devpay.user_service.entities.User;
import in.devpay.user_service.repositories.UserRepository;
import in.devpay.user_service.utils.DevpayUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class DevpayUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public DevpayUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));
        return new DevpayUserDetails(user);
    }
}
