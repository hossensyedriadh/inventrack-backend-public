package io.github.hossensyedriadh.InvenTrackRESTfulService.controller.resource.v1;

import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.Authority;
import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.ResourceCrudException;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.UserModel;
import io.github.hossensyedriadh.InvenTrackRESTfulService.pojo.PageInfo;
import io.github.hossensyedriadh.InvenTrackRESTfulService.service.CurrentAuthenticationContext;
import io.github.hossensyedriadh.InvenTrackRESTfulService.service.users.UserService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping(value = "/v1/users", produces = {MediaTypes.HAL_JSON_VALUE})
public class UserController {
    private final UserService userService;
    private final CurrentAuthenticationContext authenticationContext;

    private int defaultPageSize;

    @Autowired
    public UserController(UserService userService, CurrentAuthenticationContext authenticationContext) {
        this.userService = userService;
        this.authenticationContext = authenticationContext;
    }

    @Value("${spring.data.rest.default-page-size}")
    public void setDefaultPageSize(int defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "_links": {
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/users/?page=0&size=10&sort=username,asc",
                                        "title": "Current Page",
                                        "type": "GET"
                                    }
                                },
                                "page": {
                                    "size": 10,
                                    "totalElements": 3,
                                    "totalPages": 1,
                                    "number": 0
                                },
                                "users": [
                                    {
                                        "username": "syedriadhhossen",
                                        "enabled": true,
                                        "accountNotLocked": true,
                                        "authority": "ROLE_ADMINISTRATOR",
                                        "profile": {
                                            "id": "7e3f8a28-f3eb-4040-9401-51f85ecf1cf7",
                                            "firstName": "Syed Riadh",
                                            "lastName": "Hossen",
                                            "email": "hossensyedriadh@gmail.com",
                                            "phone": "+13456789012",
                                            "userSince": "2021-12-30",
                                            "avatar": null
                                        },
                                        "_links": {
                                            "user": {
                                                "href": "https://localhost:8443/api/v1/users?username=syedriadhhossen",
                                                "title": "Get user",
                                                "type": "GET"
                                            },
                                            "edit-form": [
                                                {
                                                    "href": "https://localhost:8443/api/v1/users/",
                                                    "title": "Update user",
                                                    "type": "PATCH"
                                                },
                                                {
                                                    "href": "https://localhost:8443/api/v1/users?username=syedriadhhossen",
                                                    "title": "Toggle user's access",
                                                    "type": "POST"
                                                }
                                            ]
                                        }
                                    }
                                ]
                            }
                            """),
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "_links": {
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/users/?page=0&size=1&sort=username,asc",
                                        "title": "Current Page",
                                        "type": "GET"
                                    },
                                    "next": {
                                        "href": "https://localhost:8443/api/v1/users/?page=1&size=1&sort=username,asc",
                                        "title": "Next Page",
                                        "type": "GET"
                                    },
                                    "last": {
                                        "href": "https://localhost:8443/api/v1/users/?page=2&size=1&sort=username,asc",
                                        "title": "Last Page",
                                        "type": "GET"
                                    }
                                },
                                "page": {
                                    "size": 1,
                                    "totalElements": 3,
                                    "totalPages": 3,
                                    "number": 0
                                },
                                "users": [
                                    {
                                        "username": "syedriadhhossen",
                                        "enabled": true,
                                        "accountNotLocked": true,
                                        "authority": "ROLE_ADMINISTRATOR",
                                        "profile": {
                                            "id": "7e3f8a28-f3eb-4040-9401-51f85ecf1cf7",
                                            "firstName": "Syed Riadh",
                                            "lastName": "Hossen",
                                            "email": "hossensyedriadh@gmail.com",
                                            "phone": "+13456789012",
                                            "userSince": "2021-12-30",
                                            "avatar": null
                                        },
                                        "_links": {
                                            "user": {
                                                "href": "https://localhost:8443/api/v1/users?username=syedriadhhossen",
                                                "title": "Get user",
                                                "type": "GET"
                                            },
                                            "edit-form": [
                                                {
                                                    "href": "https://localhost:8443/api/v1/users/",
                                                    "title": "Update user",
                                                    "type": "PATCH"
                                                },
                                                {
                                                    "href": "https://localhost:8443/api/v1/users?username=syedriadhhossen",
                                                    "title": "Toggle user's access",
                                                    "type": "POST"
                                                }
                                            ]
                                        }
                                    }
                                ]
                            }
                            """),
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "_links": {
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/users/?page=1&size=1&sort=username,asc",
                                        "title": "Current Page",
                                        "type": "GET"
                                    },
                                    "first": {
                                        "href": "https://localhost:8443/api/v1/users/?page=0&size=1&sort=username,asc",
                                        "title": "Next Page",
                                        "type": "GET"
                                    },
                                    "previous": {
                                        "href": "https://localhost:8443/api/v1/users/?page=0&size=1&sort=username,asc",
                                        "title": "Last Page",
                                        "type": "GET"
                                    },
                                    "next": {
                                        "href": "https://localhost:8443/api/v1/users/?page=2&size=1&sort=username,asc",
                                        "title": "Next Page",
                                        "type": "GET"
                                    },
                                    "last": {
                                        "href": "https://localhost:8443/api/v1/users/?page=2&size=1&sort=username,asc",
                                        "title": "Last Page",
                                        "type": "GET"
                                    }
                                },
                                "page": {
                                    "size": 1,
                                    "totalElements": 3,
                                    "totalPages": 3,
                                    "number": 1
                                },
                                "users": [
                                    {
                                        "username": "syedriadhhossen",
                                        "enabled": true,
                                        "accountNotLocked": true,
                                        "authority": "ROLE_ADMINISTRATOR",
                                        "profile": {
                                            "id": "7e3f8a28-f3eb-4040-9401-51f85ecf1cf7",
                                            "firstName": "Syed Riadh",
                                            "lastName": "Hossen",
                                            "email": "hossensyedriadh@gmail.com",
                                            "phone": "+13456789012",
                                            "userSince": "2021-12-30",
                                            "avatar": null
                                        },
                                        "_links": {
                                            "user": {
                                                "href": "https://localhost:8443/api/v1/users?username=syedriadhhossen",
                                                "title": "Get user",
                                                "type": "GET"
                                            },
                                            "edit-form": [
                                                {
                                                    "href": "https://localhost:8443/api/v1/users/",
                                                    "title": "Update user",
                                                    "type": "PATCH"
                                                },
                                                {
                                                    "href": "https://localhost:8443/api/v1/users?username=syedriadhhossen",
                                                    "title": "Toggle user's access",
                                                    "type": "POST"
                                                }
                                            ]
                                        }
                                    }
                                ]
                            }
                            """),
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "_links": {
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/users/?page=2&size=1&sort=username,asc",
                                        "title": "Current Page",
                                        "type": "GET"
                                    },
                                    "first": {
                                        "href": "https://localhost:8443/api/v1/users/?page=0&size=1&sort=username,asc",
                                        "title": "Next Page",
                                        "type": "GET"
                                    },
                                    "previous": {
                                        "href": "https://localhost:8443/api/v1/users/?page=1&size=1&sort=username,asc",
                                        "title": "Last Page",
                                        "type": "GET"
                                    }
                                },
                                "page": {
                                    "size": 1,
                                    "totalElements": 3,
                                    "totalPages": 3,
                                    "number": 2
                                },
                                "users": [
                                    {
                                        "username": "syedriadhhossen",
                                        "enabled": true,
                                        "accountNotLocked": true,
                                        "authority": "ROLE_ADMINISTRATOR",
                                        "profile": {
                                            "id": "7e3f8a28-f3eb-4040-9401-51f85ecf1cf7",
                                            "firstName": "Syed Riadh",
                                            "lastName": "Hossen",
                                            "email": "hossensyedriadh@gmail.com",
                                            "phone": "+13456789012",
                                            "userSince": "2021-12-30",
                                            "avatar": null
                                        },
                                        "_links": {
                                            "user": {
                                                "href": "https://localhost:8443/api/v1/users?username=syedriadhhossen",
                                                "title": "Get user",
                                                "type": "GET"
                                            },
                                            "edit-form": [
                                                {
                                                    "href": "https://localhost:8443/api/v1/users/",
                                                    "title": "Update user",
                                                    "type": "PATCH"
                                                },
                                                {
                                                    "href": "https://localhost:8443/api/v1/users?username=syedriadhhossen",
                                                    "title": "Toggle user's access",
                                                    "type": "POST"
                                                }
                                            ]
                                        }
                                    }
                                ]
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "GET", summary = "Get users", description = "Returns paged list of sortable user elements",
            parameters = {@Parameter(name = "page"), @Parameter(name = "size"), @Parameter(name = "sort")})
    @Cacheable(value = "userCache")
    @GetMapping("/")
    public ResponseEntity<?> users(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "size", defaultValue = "10") int size,
                                   @RequestParam(value = "sort", defaultValue = "username,asc") String... sort) {
        List<Sort.Order> sortOrders = new ArrayList<>();
        sortOrders.add(new Sort.Order(Sort.Direction.fromOptionalString(sort[1]).orElse(Sort.Direction.ASC), sort[0]));

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortOrders));

        Page<UserModel> userPage = userService.getUsers(pageable);

        if (userPage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        PageInfo pageInfo = new PageInfo(userPage);

        List<EntityModel<UserModel>> userEntityModels = new ArrayList<>();

        for (int i = 0; i < userPage.getContent().size(); i += 1) {
            if (userService.getUser(userPage.getContent().get(i).getUsername()).isPresent()) {
                UserModel user = userService.getUser(userPage.getContent().get(i).getUsername()).get();

                EntityModel<UserModel> userEntityModel = EntityModel.of(user);

                userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).user(request, user.getUsername()))
                        .withRel("user").withTitle("Get user").withType(HttpMethod.GET.toString()));

                if (authenticationContext.getAuthenticatedUser().getAuthority().equals(Authority.ROLE_ROOT)) {
                    userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(request, new UserModel()))
                            .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update user").withType(HttpMethod.PATCH.toString()));

                    userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).user(request, user.getUsername()))
                            .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Toggle user's access").withType(HttpMethod.POST.toString()));
                }

                userEntityModels.add(userEntityModel);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("users", userEntityModels);
        response.put("page", pageInfo);

        EntityModel<?> responseModel = EntityModel.of(response);

        responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass())
                        .users(request, page, size, UserModel.class.getDeclaredFields()[1].getName(), sort[1]))
                .withRel(IanaLinkRelations.SELF).withType(HttpMethod.GET.toString()).withTitle("Current Page"));

        if (page > 0 && page <= (userPage.getTotalPages() - 1) && userPage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass())
                            .users(request, 0, size, UserModel.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.FIRST).withType(HttpMethod.GET.toString()).withTitle("First Page"));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass())
                            .users(request, page - 1, size, UserModel.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.PREVIOUS).withType(HttpMethod.GET.toString()).withTitle("Previous Page"));
        }

        if (page < (userPage.getTotalPages() - 1) && userPage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass())
                            .users(request, page + 1, size, UserModel.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.NEXT).withType(HttpMethod.GET.toString()).withTitle("Next Page"));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass())
                            .users(request, (userPage.getTotalPages() - 1), size, UserModel.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.LAST).withType(HttpMethod.GET.toString()).withTitle("Last Page"));
        }

        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "username": "syedriadhhossen",
                                "enabled": true,
                                "accountNotLocked": true,
                                "authority": "ROLE_ADMINISTRATOR",
                                "profile": {
                                    "id": "7e3f8a28-f3eb-4040-9401-51f85ecf1cf7",
                                    "firstName": "Syed Riadh",
                                    "lastName": "Hossen",
                                    "email": "hossensyedriadh@gmail.com",
                                    "phone": "+13456789012",
                                    "userSince": "2021-12-30",
                                    "avatar": null
                                },
                                "_links": {
                                    "users": {
                                        "href": "https://localhost:8443/api/v1/users/?page=0&size=10&sort=username,asc",
                                        "title": "Get users",
                                        "type": "GET"
                                    },
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/users?username=syedriadhhossen",
                                        "title": "Get user",
                                        "type": "GET"
                                    },
                                    "edit-form": [
                                        {
                                            "href": "https://localhost:8443/api/v1/users/",
                                            "title": "Update user",
                                            "type": "PATCH"
                                        },
                                        {
                                            "href": "https://localhost:8443/api/v1/users?username=syedriadhhossen",
                                            "title": "Toggle user's access",
                                            "type": "POST"
                                        }
                                    ]
                                }
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "GET", summary = "Get user by username",
            description = "Returns a user by given username", parameters = {@Parameter(name = "username", required = true)})
    @Cacheable(value = "userCache", key = "#username")
    @GetMapping(params = {"username"})
    public ResponseEntity<?> user(HttpServletRequest request, @RequestParam("username") String username) {
        Optional<UserModel> user = userService.getUser(username);

        if (user.isPresent()) {
            EntityModel<UserModel> userEntityModel = EntityModel.of(user.get());

            userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).users(request, 0, defaultPageSize,
                            UserModel.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                    .withRel("users").withTitle("Get users")
                    .withType(HttpMethod.GET.toString()));

            userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).user(request, user.get().getUsername()))
                    .withRel(IanaLinkRelations.SELF).withTitle("Get user").withType(HttpMethod.GET.toString()));

            if (authenticationContext.getAuthenticatedUser().getAuthority().equals(Authority.ROLE_ROOT)) {
                userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(request, user.get()))
                        .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update user").withType(HttpMethod.PATCH.toString()));

                userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).user(request, user.get().getUsername()))
                        .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Toggle user's access").withType(HttpMethod.POST.toString()));
            }

            return new ResponseEntity<>(userEntityModel, HttpStatus.OK);
        }

        throw new ResourceCrudException("User not found with username: " + username, HttpStatus.NO_CONTENT,
                request.getRequestURI());
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "username": "syedriadhhossen",
                                "enabled": true,
                                "accountNotLocked": true,
                                "authority": "ROLE_ADMINISTRATOR",
                                "profile": {
                                    "id": "7e3f8a28-f3eb-4040-9401-51f85ecf1cf7",
                                    "firstName": "Syed Riadh",
                                    "lastName": "Hossen",
                                    "email": "hossensyedriadh@gmail.com",
                                    "phone": "+13456789012",
                                    "userSince": "2021-12-30",
                                    "avatar": null
                                },
                                "_links": {
                                    "users": {
                                        "href": "https://localhost:8443/api/v1/users/?page=0&size=10&sort=username,asc",
                                        "title": "Get users",
                                        "type": "GET"
                                    },
                                    "item": {
                                        "href": "https://localhost:8443/api/v1/users?username=syedriadhhossen",
                                        "title": "Get user",
                                        "type": "GET"
                                    },
                                    "self": [
                                        {
                                            "href": "https://localhost:8443/api/v1/users/",
                                            "title": "Update user",
                                            "type": "PATCH"
                                        },
                                        {
                                            "href": "https://localhost:8443/api/v1/users?username=syedriadhhossen",
                                            "title": "Toggle user's access",
                                            "type": "POST"
                                        }
                                    ]
                                }
                            }
                            """)
            })),
            @ApiResponse(code = 400, message = "Bad Request", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "status": 400,
                                "timestamp": "31-12-2021 12:59:00",
                                "message": "User not found with username: syedriaddhossen",
                                "error": "Bad Request",
                                "path": "/api/v1/users/"
                            }
                            """)
            })),
            @ApiResponse(code = 403, message = "Forbidden", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "timestamp": "2022-01-14T22:10:30.731+00:00",
                                "status": 403,
                                "error": "Forbidden",
                                "message": "You do not have permission to access this resource",
                                "path": "/api/v1/users/"
                            }
                            """)
            }))
    })
    @Operation(method = "PATCH", summary = "Update a user",
            description = "Updates a user information using updatable fields and returns the updated user")
    @PreAuthorize("hasAuthority('ROLE_ROOT')")
    @CachePut(value = "userCache", key = "#userModel.username")
    @PatchMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(HttpServletRequest request, @RequestBody UserModel userModel) {
        Optional<UserModel> updatedUser = userService.update(userModel);

        if (updatedUser.isPresent()) {
            EntityModel<UserModel> userEntityModel = EntityModel.of(updatedUser.get());

            userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).users(request, 0, defaultPageSize,
                            UserModel.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                    .withRel("users").withTitle("Get users")
                    .withType(HttpMethod.GET.toString()));

            userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).user(request, updatedUser.get().getUsername()))
                    .withRel("user").withTitle("Get user").withMedia(MediaTypes.HAL_JSON_VALUE)
                    .withType(HttpMethod.GET.toString()));

            userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(request, updatedUser.get()))
                    .withRel(IanaLinkRelations.SELF).withTitle("Update user").withType(HttpMethod.PATCH.toString()));

            userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).user(request, updatedUser.get().getUsername()))
                    .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Toggle user's access").withType(HttpMethod.POST.toString()));

            return new ResponseEntity<>(userEntityModel, HttpStatus.OK);
        }

        throw new ResourceCrudException("User not found with username: " + userModel.getUsername(), HttpStatus.BAD_REQUEST,
                request.getRequestURI());
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "username": "syedriadhhossen",
                                "enabled": false,
                                "accountNotLocked": true,
                                "authority": "ROLE_ADMINISTRATOR",
                                "profile": {
                                    "id": "7e3f8a28-f3eb-4040-9401-51f85ecf1cf7",
                                    "firstName": "Syed Riadh",
                                    "lastName": "Hossen",
                                    "email": "hossensyedriadh@gmail.com",
                                    "phone": "+13456789012",
                                    "userSince": "2021-12-30",
                                    "avatar": null
                                },
                                "_links": {
                                    "users": {
                                        "href": "https://localhost:8443/api/v1/users/?page=0&size=10&sort=username,asc",
                                        "title": "Get users",
                                        "type": "GET"
                                    },
                                    "item": {
                                        "href": "https://localhost:8443/api/v1/users?username=syedriadhhossen",
                                        "title": "Get user",
                                        "type": "GET"
                                    },
                                    "self": [
                                        {
                                            "href": "https://localhost:8443/api/v1/users/",
                                            "title": "Update user",
                                            "type": "PATCH"
                                        },
                                        {
                                            "href": "https://localhost:8443/api/v1/users?username=syedriadhhossen",
                                            "title": "Toggle user's access",
                                            "type": "POST"
                                        }
                                    ]
                                }
                            }
                            """)
            })),
            @ApiResponse(code = 400, message = "Bad Request", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "status": 400,
                                "timestamp": "31-12-2021 12:59:00",
                                "message": "User not found with username: syedriaddhossen",
                                "error": "Bad Request",
                                "path": "/api/v1/users/"
                            }
                            """)
            })),
            @ApiResponse(code = 403, message = "Forbidden", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "timestamp": "2022-01-14T22:10:30.731+00:00",
                                "status": 403,
                                "error": "Forbidden",
                                "message": "You do not have permission to access this resource",
                                "path": "/api/v1/users/"
                            }
                            """)
            }))
    })
    @Operation(method = "POST", summary = "Toggle access of a user",
            description = "Toggles access flags of a user and returns the user information")
    @CachePut(value = "userCache", key = "#username")
    @PreAuthorize("hasAuthority('ROLE_ROOT')")
    @PostMapping(value = "/toggle-access/{username}")
    public ResponseEntity<?> toggleAccess(HttpServletRequest request, @PathVariable("username") String username) {
        Optional<UserModel> user = userService.toggleAccess(username);

        if (user.isPresent()) {
            EntityModel<UserModel> userEntityModel = EntityModel.of(user.get());

            userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).users(request, 0, defaultPageSize,
                            UserModel.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                    .withRel("users").withTitle("Get users").withType(HttpMethod.GET.toString()));

            userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).user(request, user.get().getUsername()))
                    .withRel("user").withTitle("Get user").withType(HttpMethod.GET.toString()));

            userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(request, user.get()))
                    .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update user").withType(HttpMethod.PATCH.toString()));

            userEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).user(request, user.get().getUsername()))
                    .withRel(IanaLinkRelations.SELF).withTitle("Toggle user's access").withType(HttpMethod.POST.toString()));

            return new ResponseEntity<>(userEntityModel, HttpStatus.OK);
        } else {
            throw new ResourceCrudException("User not found with username: " + username, HttpStatus.BAD_REQUEST,
                    request.getRequestURI());
        }
    }
}
