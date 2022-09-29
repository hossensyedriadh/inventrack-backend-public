package io.github.hossensyedriadh.inventrackrestfulservice.service.users;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.User;
import io.github.hossensyedriadh.inventrackrestfulservice.model.UserRoleChangeRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<User> users(Pageable pageable);

    User user(String username);

    User update(User user);

    User toggleAccess(String username);

    User changeUserRole(UserRoleChangeRequest roleChangeRequest);
}
