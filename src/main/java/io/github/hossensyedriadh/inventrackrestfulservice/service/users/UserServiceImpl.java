package io.github.hossensyedriadh.inventrackrestfulservice.service.users;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.User;
import io.github.hossensyedriadh.inventrackrestfulservice.enumerator.Authority;
import io.github.hossensyedriadh.inventrackrestfulservice.enumerator.InvitedUserAuthority;
import io.github.hossensyedriadh.inventrackrestfulservice.exception.ResourceException;
import io.github.hossensyedriadh.inventrackrestfulservice.model.UserRoleChangeRequest;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, HttpServletRequest httpServletRequest) {
        this.userRepository = userRepository;
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public Page<User> users(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User user(String username) {
        if (userRepository.findById(username).isPresent()) {
            return userRepository.findById(username).get();
        }

        throw new ResourceException("User not found with username: " + username, HttpStatus.BAD_REQUEST, httpServletRequest);
    }

    @Override
    public User update(User user) {
        if (userRepository.findById(user.getUsername()).isPresent()) {
            return userRepository.saveAndFlush(user);
        }

        throw new ResourceException("User not found with username: " + user.getUsername(), HttpStatus.BAD_REQUEST, httpServletRequest);
    }

    @Override
    public User toggleAccess(String username) {
        if (userRepository.findById(username).isPresent()) {
            User user = userRepository.findById(username).get();
            user.setEnabled(!user.isEnabled());
            return userRepository.saveAndFlush(user);
        }

        throw new ResourceException("User not found with username: " + username, HttpStatus.BAD_REQUEST, httpServletRequest);
    }

    @Override
    public User changeUserRole(UserRoleChangeRequest roleChangeRequest) {
        if (userRepository.findById(roleChangeRequest.getUsername()).isPresent()) {
            User user = userRepository.findById(roleChangeRequest.getUsername()).get();
            user.setAuthority(roleChangeRequest.getRole() == InvitedUserAuthority.ROLE_ADMINISTRATOR ? Authority.ROLE_ADMINISTRATOR : Authority.ROLE_MODERATOR);
            userRepository.saveAndFlush(user);
        }

        throw new ResourceException("User not found with username: " + roleChangeRequest.getUsername(), HttpStatus.BAD_REQUEST, httpServletRequest);
    }
}
