package io.github.hossensyedriadh.inventrackrestfulservice.service;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.User;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CurrentAuthenticationContext {
    private final UserRepository userRepository;

    @Autowired
    public CurrentAuthenticationContext(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String username = ((UserDetails) principal).getUsername();

        if (userRepository.findById(username).isPresent()) {
            return userRepository.findById(username).get();
        }

        throw new UsernameNotFoundException("User not found");
    }
}
