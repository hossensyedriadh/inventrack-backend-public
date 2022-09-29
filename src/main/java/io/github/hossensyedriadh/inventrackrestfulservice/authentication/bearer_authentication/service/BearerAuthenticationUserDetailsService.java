package io.github.hossensyedriadh.inventrackrestfulservice.authentication.bearer_authentication.service;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.User;
import io.github.hossensyedriadh.inventrackrestfulservice.exception.UserAccountLockedException;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.ProfileRepository;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.UserRepository;
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
public class BearerAuthenticationUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    @Autowired
    public BearerAuthenticationUserDetailsService(UserRepository userRepository, ProfileRepository profileRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    /**
     * Locates the user based on the username. In the actual implementation, the search
     * may be case-sensitive, or case-insensitive depending on how the
     * implementation instance is configured. In this case, the <code>UserDetails</code>
     * object that comes back may have a username that is of a different case than what
     * was actually requested..
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (never <code>null</code>)
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     *                                   GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        boolean isUsername = username.matches("^[a-zA-Z_]{4,75}$");
        boolean isEmail = username.matches("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+$");

        if (isUsername) {
            if (userRepository.findById(username).isPresent()) {
                User user = userRepository.findById(username).get();
                GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getAuthority().toString());

                UserDetails userDetails = new UserDetails() {
                    @Serial
                    private static final long serialVersionUID = -1998763782490959572L;

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
                    throw new UsernameNotFoundException("User not found: " + username);
                }

                if (!user.isAccountNotLocked()) {
                    throw new UserAccountLockedException("User account locked: " + username);
                }

                return org.springframework.security.core.userdetails.User.withUserDetails(userDetails).build();
            } else {
                throw new UsernameNotFoundException("User not found: " + username);
            }
        } else if (isEmail) {

            if (profileRepository.findAll().stream().filter(profile -> profile.getEmail().equals(username)).count() == 1) {
                User user = userRepository.findAll().stream().filter(usr -> usr.getProfile().getEmail().equals(username)).toList().get(0);
                GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getAuthority().toString());

                UserDetails userDetails = new UserDetails() {
                    @Serial
                    private static final long serialVersionUID = 160298685343041673L;

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
                    throw new UsernameNotFoundException("User not found: " + username);
                }

                if (!user.isAccountNotLocked()) {
                    throw new UserAccountLockedException("User account locked: " + username);
                }

                return org.springframework.security.core.userdetails.User.withUserDetails(userDetails).build();
            } else {
                throw new UsernameNotFoundException("User not found: " + username);
            }
        } else {
            throw new UsernameNotFoundException("User not found: " + username);
        }
    }
}
