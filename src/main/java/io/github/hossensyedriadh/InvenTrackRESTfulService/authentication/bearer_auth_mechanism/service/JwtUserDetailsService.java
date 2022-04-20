package io.github.hossensyedriadh.InvenTrackRESTfulService.authentication.bearer_auth_mechanism.service;

import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.User;
import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.UserAccountLockedException;
import io.github.hossensyedriadh.InvenTrackRESTfulService.repository.ProfileRepository;
import io.github.hossensyedriadh.InvenTrackRESTfulService.repository.UserRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.Serial;
import java.util.Collection;
import java.util.Collections;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    private final ObjectFactory<UserRepository> userRepositoryObjectFactory;
    private final ObjectFactory<ProfileRepository> profileRepositoryObjectFactory;

    @Autowired
    public JwtUserDetailsService(ObjectFactory<UserRepository> userRepositoryObjectFactory,
                                 ObjectFactory<ProfileRepository> profileRepositoryObjectFactory) {
        this.userRepositoryObjectFactory = userRepositoryObjectFactory;
        this.profileRepositoryObjectFactory = profileRepositoryObjectFactory;
    }

    /**
     * Locates the user based on the username. In the actual implementation, the search may be case-sensitive, or
     * case-insensitive depending on how the implementation instance is configured. In this case, the
     * <code>UserDetails</code> object that comes back may have a username that is of a different case than what was
     * actually requested.
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (never <code>null</code>)
     * @throws UsernameNotFoundException if the user could not be found or the user has no GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (userRepositoryObjectFactory.getObject().findById(username).isPresent()) {
            User user = userRepositoryObjectFactory.getObject().findById(username).get();
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getAuthority().toString());

            UserDetails userDetails = new UserDetails() {
                @Serial
                private static final long serialVersionUID = 8931116173449483147L;

                @Override
                public Collection<? extends GrantedAuthority> getAuthorities() {
                    return Collections.singletonList(grantedAuthority);
                }

                @Override
                public String getPassword() {
                    return user.getPassword();
                }

                @Override
                public String getUsername() {
                    return user.getUsername();
                }

                @Override
                public boolean isAccountNonExpired() {
                    return true;
                }

                @Override
                public boolean isAccountNonLocked() {
                    return user.isAccountNotLocked();
                }

                @Override
                public boolean isCredentialsNonExpired() {
                    return false;
                }

                @Override
                public boolean isEnabled() {
                    return user.isEnabled();
                }
            };

            if (!user.isEnabled()) {
                throw new UsernameNotFoundException("User not found");
            }

            if (!user.isAccountNotLocked()) {
                throw new UserAccountLockedException("Account is locked");
            }

            return org.springframework.security.core.userdetails.User.withUserDetails(userDetails).build();
        } else {
            if (profileRepositoryObjectFactory.getObject().findAll().stream().filter((profile) -> profile.getEmail().equals(username)).count() == 1) {
                User user = userRepositoryObjectFactory.getObject().findAll().stream().filter((ua) -> ua.getProfile().getEmail().equals(username)).toList().get(0);
                GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getAuthority().toString());

                UserDetails userDetails = new UserDetails() {
                    @Serial
                    private static final long serialVersionUID = -4891237439497984713L;

                    @Override
                    public Collection<? extends GrantedAuthority> getAuthorities() {
                        return Collections.singletonList(grantedAuthority);
                    }

                    @Override
                    public String getPassword() {
                        return user.getPassword();
                    }

                    @Override
                    public String getUsername() {
                        return user.getUsername();
                    }

                    @Override
                    public boolean isAccountNonExpired() {
                        return true;
                    }

                    @Override
                    public boolean isAccountNonLocked() {
                        return user.isAccountNotLocked();
                    }

                    @Override
                    public boolean isCredentialsNonExpired() {
                        return true;
                    }

                    @Override
                    public boolean isEnabled() {
                        return user.isEnabled();
                    }
                };

                if (!user.isEnabled()) {
                    throw new UsernameNotFoundException("User not found");
                }

                if (!user.isAccountNotLocked()) {
                    throw new UserAccountLockedException("Account is locked");
                }

                return org.springframework.security.core.userdetails.User.withUserDetails(userDetails).build();
            }
        }

        throw new UsernameNotFoundException("User not found");
    }
}
