package vn.thentrees.backendservice.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import vn.thentrees.backendservice.repository.UserRepository;

@Service
public record UserDetailService(UserRepository userRepository) {
    public UserDetailsService userDetailService() {
        return  userRepository::findByUsername;
    }
}
