package io.github.hossensyedriadh.InvenTrackRESTfulService.service.users;

import io.github.hossensyedriadh.InvenTrackRESTfulService.model.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public sealed interface UserService permits UserServiceImpl {
    Page<UserModel> getUsers(Pageable pageable);

    Optional<UserModel> getUser(String username);

    Optional<UserModel> update(UserModel userModel);

    Optional<UserModel> toggleAccess(String username);
}
