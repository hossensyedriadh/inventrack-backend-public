package io.github.hossensyedriadh.InvenTrackRESTfulService.service;

import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.User;
import io.github.hossensyedriadh.InvenTrackRESTfulService.repository.UserRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CurrentAuthenticationContext {
    private final ObjectFactory<UserRepository> userRepositoryObjectFactory;

    @Autowired
    public CurrentAuthenticationContext(ObjectFactory<UserRepository> userRepositoryObjectFactory) {
        this.userRepositoryObjectFactory = userRepositoryObjectFactory;
    }

    public User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String username = ((UserDetails) principal).getUsername();

        if (userRepositoryObjectFactory.getObject().findById(username).isPresent()) {
            return userRepositoryObjectFactory.getObject().findById(username).get();
        }

        throw new UsernameNotFoundException("User not found");
    }
}
