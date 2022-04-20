package io.github.hossensyedriadh.InvenTrackRESTfulService.service.users;

import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.User;
import io.github.hossensyedriadh.InvenTrackRESTfulService.mapper.entityToModel.UserToUserModel;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.UserModel;
import io.github.hossensyedriadh.InvenTrackRESTfulService.repository.UserRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public final class UserServiceImpl implements UserService {
    private final ObjectFactory<UserRepository> userRepositoryObjectFactory;
    private final UserToUserModel toModel;

    @Autowired
    public UserServiceImpl(ObjectFactory<UserRepository> userRepositoryObjectFactory,
                           UserToUserModel toModel) {
        this.userRepositoryObjectFactory = userRepositoryObjectFactory;
        this.toModel = toModel;
    }

    @Override
    public Page<UserModel> getUsers(Pageable pageable) {
        Page<User> userPage = userRepositoryObjectFactory.getObject().findAll(pageable);

        return userPage.map(toModel::convert);
    }

    @Override
    public Optional<UserModel> getUser(String username) {
        if (userRepositoryObjectFactory.getObject().findById(username).isPresent()) {
            return Optional.ofNullable(this.toModel.convert(userRepositoryObjectFactory.getObject().getById(username)));
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserModel> update(UserModel userModel) {
        if (userRepositoryObjectFactory.getObject().findById(userModel.getUsername()).isPresent()) {
            User user = userRepositoryObjectFactory.getObject().getById(userModel.getUsername());
            user.getProfile().setEmail((userModel.getProfile().getEmail() != null) ?
                    userModel.getProfile().getEmail() : user.getProfile().getEmail());
            user.getProfile().setPhoneNo((userModel.getProfile().getPhone() != null) ?
                    userModel.getProfile().getPhone() : user.getProfile().getPhoneNo());
            userRepositoryObjectFactory.getObject().saveAndFlush(user);

            User updated = userRepositoryObjectFactory.getObject().getById(userModel.getUsername());

            if (updated.getProfile().getEmail().equals(userModel.getProfile().getEmail())
                    && updated.getProfile().getPhoneNo().equals(userModel.getProfile().getPhone())) {
                return Optional.ofNullable(this.toModel.convert(updated));
            }

            return Optional.empty();
        }

        throw new UsernameNotFoundException("User not found with username: " + userModel.getUsername());
    }

    @Override
    public Optional<UserModel> toggleAccess(String username) {
        if (userRepositoryObjectFactory.getObject().findById(username).isPresent()) {
            User user = userRepositoryObjectFactory.getObject().getById(username);
            boolean currentStatus = user.isEnabled();
            user.setEnabled(!user.isEnabled());
            userRepositoryObjectFactory.getObject().saveAndFlush(user);

            User updated = userRepositoryObjectFactory.getObject().getById(username);

            if (updated.isEnabled() != currentStatus) {
                return Optional.ofNullable(this.toModel.convert(updated));
            }
            return Optional.empty();
        }
        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
