package io.github.hossensyedriadh.inventrackrestfulservice.controller.resource.v1;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.User;
import io.github.hossensyedriadh.inventrackrestfulservice.enumerator.Authority;
import io.github.hossensyedriadh.inventrackrestfulservice.model.PageInfo;
import io.github.hossensyedriadh.inventrackrestfulservice.model.UserRoleChangeRequest;
import io.github.hossensyedriadh.inventrackrestfulservice.service.CurrentAuthenticationContext;
import io.github.hossensyedriadh.inventrackrestfulservice.service.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/v1/users", produces = {MediaTypes.HAL_JSON_VALUE})
public class UserController {
    private final UserService userService;
    private final CurrentAuthenticationContext authenticationContext;

    @Autowired
    public UserController(UserService userService, CurrentAuthenticationContext authenticationContext) {
        this.userService = userService;
        this.authenticationContext = authenticationContext;
    }

    private int defaultPageSize;

    @Value("${spring.data.rest.default-page-size}")
    public void setDefaultPageSize(int defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/")
    public ResponseEntity<?> users(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "size", defaultValue = "10") int size,
                                   @RequestParam(value = "sort", defaultValue = "username,asc") String... sort) {
        List<Sort.Order> sortOrders = new ArrayList<>();
        sortOrders.add(new Sort.Order(Sort.Direction.fromOptionalString(sort[1]).orElse(Sort.Direction.ASC), sort[0]));

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortOrders));

        Page<User> userPage = userService.users(pageable);

        if (userPage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        PageInfo pageInfo = new PageInfo(userPage);

        List<EntityModel<User>> userEntityModels = new ArrayList<>();

        for (int i = 0; i < userPage.getContent().size(); i += 1) {
            User user = userService.user(userPage.getContent().get(i).getUsername());

            EntityModel<User> userEntityModel = EntityModel.of(user);

            userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).user(user.getUsername()))
                    .withRel("user").withTitle("Get user").withType(HttpMethod.GET.toString()));

            if (authenticationContext.getAuthenticatedUser().getAuthority().equals(Authority.ROLE_ROOT)) {
                userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(new User()))
                        .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update user").withType(HttpMethod.PATCH.toString()));

                userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).user(user.getUsername()))
                        .withRel("toggle-access").withTitle("Toggle user's access").withType(HttpMethod.POST.toString()));

                userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).changeRole(new UserRoleChangeRequest()))
                        .withRel("change-role").withTitle("Change user's role").withType(HttpMethod.POST.toString()));
            }

            userEntityModels.add(userEntityModel);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("users", userEntityModels);
        response.put("page", pageInfo);

        EntityModel<?> responseModel = EntityModel.of(response);

        responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass())
                        .users(page, size, User.class.getDeclaredFields()[1].getName(), sort[1]))
                .withRel(IanaLinkRelations.SELF).withType(HttpMethod.GET.toString()).withTitle("Current Page"));

        if (page > 0 && page <= (userPage.getTotalPages() - 1) && userPage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass())
                            .users(0, size, User.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.FIRST).withType(HttpMethod.GET.toString()).withTitle("First Page"));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass())
                            .users(page - 1, size, User.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.PREVIOUS).withType(HttpMethod.GET.toString()).withTitle("Previous Page"));
        }

        if (page < (userPage.getTotalPages() - 1) && userPage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass())
                            .users(page + 1, size, User.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.NEXT).withType(HttpMethod.GET.toString()).withTitle("Next Page"));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass())
                            .users((userPage.getTotalPages() - 1), size, User.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.LAST).withType(HttpMethod.GET.toString()).withTitle("Last Page"));
        }

        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> user(@PathVariable("username") String username) {
        User user = userService.user(username);

        EntityModel<User> userEntityModel = EntityModel.of(user);

        userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).users(0, defaultPageSize,
                        User.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                .withRel("users").withTitle("Get users")
                .withType(HttpMethod.GET.toString()));

        userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).user(user.getUsername()))
                .withRel(IanaLinkRelations.SELF).withTitle("Get user").withType(HttpMethod.GET.toString()));

        if (authenticationContext.getAuthenticatedUser().getAuthority().equals(Authority.ROLE_ROOT)) {
            userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(user))
                    .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update user").withType(HttpMethod.PATCH.toString()));

            userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).user(user.getUsername()))
                    .withRel("toggle-access").withTitle("Toggle user's access").withType(HttpMethod.POST.toString()));

            userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).changeRole(new UserRoleChangeRequest()))
                    .withRel("change-role").withTitle("Change user's role").withType(HttpMethod.POST.toString()));
        }

        return new ResponseEntity<>(userEntityModel, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ROLE_ROOT')")
    @PatchMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(@Valid @RequestBody User user) {
        User updatedUser = userService.update(user);

        EntityModel<User> userEntityModel = EntityModel.of(updatedUser);

        userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).users(0, defaultPageSize,
                        User.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                .withRel("users").withTitle("Get users")
                .withType(HttpMethod.GET.toString()));

        userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).user(updatedUser.getUsername()))
                .withRel("user").withTitle("Get user").withMedia(MediaTypes.HAL_JSON_VALUE)
                .withType(HttpMethod.GET.toString()));

        userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(updatedUser))
                .withRel(IanaLinkRelations.SELF).withTitle("Update user").withType(HttpMethod.PATCH.toString()));

        userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).user(updatedUser.getUsername()))
                .withRel("toggle-access").withTitle("Toggle user's access").withType(HttpMethod.POST.toString()));

        userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).changeRole(new UserRoleChangeRequest()))
                .withRel("change-role").withTitle("Change user's role").withType(HttpMethod.POST.toString()));

        return new ResponseEntity<>(userEntityModel, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ROLE_ROOT')")
    @PostMapping(value = "/toggle-access/{username}")
    public ResponseEntity<?> toggleAccess(@PathVariable("username") String username) {
        User user = userService.toggleAccess(username);

        EntityModel<User> userEntityModel = EntityModel.of(user);

        userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).users(0, defaultPageSize,
                        User.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                .withRel("users").withTitle("Get users").withType(HttpMethod.GET.toString()));

        userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).user(user.getUsername()))
                .withRel("user").withTitle("Get user").withType(HttpMethod.GET.toString()));

        userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(user))
                .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update user").withType(HttpMethod.PATCH.toString()));

        userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).user(user.getUsername()))
                .withRel(IanaLinkRelations.SELF).withTitle("Toggle user's access").withType(HttpMethod.POST.toString()));

        userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).changeRole(new UserRoleChangeRequest()))
                .withRel("change-role").withTitle("Change user's role").withType(HttpMethod.POST.toString()));

        return new ResponseEntity<>(userEntityModel, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ROLE_ROOT')")
    @PostMapping(value = "/change-role", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> changeRole(@Valid @RequestBody UserRoleChangeRequest roleChangeRequest) {
        User user = userService.changeUserRole(roleChangeRequest);

        EntityModel<User> userEntityModel = EntityModel.of(user);

        userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).users(0, defaultPageSize,
                        User.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                .withRel("users").withTitle("Get users").withType(HttpMethod.GET.toString()));

        userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).user(user.getUsername()))
                .withRel("user").withTitle("Get user").withType(HttpMethod.GET.toString()));

        userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(user))
                .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update user").withType(HttpMethod.PATCH.toString()));

        userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).user(user.getUsername()))
                .withRel("toggle-access").withTitle("Toggle user's access").withType(HttpMethod.POST.toString()));

        userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).changeRole(new UserRoleChangeRequest()))
                .withRel(IanaLinkRelations.SELF).withTitle("Change user's role").withType(HttpMethod.POST.toString()));

        return new ResponseEntity<>(userEntityModel, HttpStatus.OK);
    }
}
