package io.github.hossensyedriadh.InvenTrackRESTfulService.controller.resource.v1;

import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.SignupInvitationStatus;
import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.ResourceCrudException;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.SignupInvitationModel;
import io.github.hossensyedriadh.InvenTrackRESTfulService.pojo.PageInfo;
import io.github.hossensyedriadh.InvenTrackRESTfulService.service.invitations.InvitationService;
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

@PreAuthorize("hasAuthority('ROLE_ROOT')")
@RestController
@RequestMapping(value = "/v1/invitations", produces = {MediaTypes.HAL_JSON_VALUE})
public class InvitationController {
    private final InvitationService invitationService;
    private int defaultPageSize;

    @Autowired
    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
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
                                        "href": "https://localhost:8443/api/v1/invitations/?page=0&size=1&sort=id,asc",
                                        "title": "Current Page",
                                        "type": "GET"
                                    }
                                },
                                "invitations": [
                                    {
                                        "id": "82369f274a742e8e",
                                        "createdOn": "2022-01-09T00:15:12",
                                        "expiresOn": "2022-01-10T00:15:12",
                                        "status": "INVALID",
                                        "invalidationRemarks": "EXPIRED",
                                        "invalidatedOn": "2022-01-10T00:15:12",
                                        "recipientEmail": "hossensyedriadh@gmail.com",
                                        "forAuthority": "ROLE_MODERATOR",
                                        "createdBy": "syedriadhhossen",
                                        "_links": {
                                            "item": {
                                                "href": "https://localhost:8443/api/v1/invitations?id=82369f274a742e8e",
                                                "title": "Get invitation",
                                                "type": "GET"
                                            },
                                            "create-form": {
                                                "href": "https://localhost:8443/api/v1/invitations/",
                                                "title": "Create invitation",
                                                "type": "POST"
                                            }
                                        }
                                    }
                                ],
                                "page": {
                                    "size": 1,
                                    "totalElements": 1,
                                    "totalPages": 1,
                                    "number": 0
                                }
                            }
                            """),
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "_links": {
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/invitations/?page=0&size=1&sort=id,asc",
                                        "title": "Current Page",
                                        "type": "GET"
                                    },
                                    "next": {
                                        "href": "https://localhost:8443/api/v1/invitations/?page=1&size=1&sort=id,asc",
                                        "title": "Next Page",
                                        "type": "GET"
                                    },
                                    "last": {
                                        "href": "https://localhost:8443/api/v1/invitations/?page=4&size=1&sort=id,asc",
                                        "title": "Last Page",
                                        "type": "GET"
                                    }
                                },
                                "invitations": [
                                    {
                                        "id": "82369f274a742e8e",
                                        "createdOn": "2022-01-09T00:15:12",
                                        "expiresOn": "2022-01-10T00:15:12",
                                        "status": "INVALID",
                                        "invalidationRemarks": "EXPIRED",
                                        "invalidatedOn": "2022-01-10T00:15:12",
                                        "recipientEmail": "hossensyedriadh@gmail.com",
                                        "forAuthority": "ROLE_MODERATOR",
                                        "createdBy": "syedriadhhossen",
                                        "_links": {
                                            "item": {
                                                "href": "https://localhost:8443/api/v1/invitations?id=82369f274a742e8e",
                                                "title": "Get invitation",
                                                "type": "GET"
                                            },
                                            "create-form": {
                                                "href": "https://localhost:8443/api/v1/invitations/",
                                                "title": "Create invitation",
                                                "type": "POST"
                                            }
                                        }
                                    }
                                ],
                                "page": {
                                    "size": 1,
                                    "totalElements": 3,
                                    "totalPages": 3,
                                    "number": 0
                                }
                            }
                            """),
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "_links": {
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/invitations/?page=1&size=1&sort=id,asc",
                                        "title": "Current Page",
                                        "type": "GET"
                                    },
                                    "first": {
                                        "href": "https://localhost:8443/api/v1/invitations/?page=0&size=1&sort=id,asc",
                                        "title": "First Page",
                                        "type": "GET"
                                    },
                                    "previous": {
                                        "href": "https://localhost:8443/api/v1/invitations/?page=0&size=1&sort=id,asc",
                                        "title": "Previous Page",
                                        "type": "GET"
                                    },
                                    "next": {
                                        "href": "https://localhost:8443/api/v1/invitations/?page=2&size=1&sort=id,asc",
                                        "title": "Next Page",
                                        "type": "GET"
                                    },
                                    "last": {
                                        "href": "https://localhost:8443/api/v1/invitations/?page=4&size=1&sort=id,asc",
                                        "title": "Last Page",
                                        "type": "GET"
                                    }
                                },
                                "invitations": [
                                    {
                                        "id": "864777487914d574",
                                        "createdOn": "2022-01-08T19:31:09",
                                        "expiresOn": "2022-01-08T19:31:09",
                                        "status": "INVALID",
                                        "invalidationRemarks": "USED",
                                        "invalidatedOn": "2022-01-08T19:40:44",
                                        "recipientEmail": "riadhhossen@gmail.com",
                                        "forAuthority": "ROLE_MODERATOR",
                                        "createdBy": "syedriadhhossen",
                                        "_links": {
                                            "item": {
                                                "href": "https://localhost:8443/api/v1/invitations?id=864777487914d574",
                                                "title": "Get invitation",
                                                "type": "GET"
                                            },
                                            "create-form": {
                                                "href": "https://localhost:8443/api/v1/invitations/",
                                                "title": "Create invitation",
                                                "type": "POST"
                                            }
                                        }
                                    }
                                ],
                                "page": {
                                    "size": 1,
                                    "totalElements": 3,
                                    "totalPages": 3,
                                    "number": 1
                                }
                            }
                            """),
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "_links": {
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/invitations/?page=4&size=1&sort=id,asc",
                                        "title": "Current Page",
                                        "type": "GET"
                                    },
                                    "first": {
                                        "href": "https://localhost:8443/api/v1/invitations/?page=0&size=1&sort=id,asc",
                                        "title": "First Page",
                                        "type": "GET"
                                    },
                                    "previous": {
                                        "href": "https://localhost:8443/api/v1/invitations/?page=3&size=1&sort=id,asc",
                                        "title": "Previous Page",
                                        "type": "GET"
                                    }
                                },
                                "invitations": [
                                    {
                                        "id": "bf81645ba06cef85",
                                        "createdOn": "2022-01-08T20:24:50",
                                        "expiresOn": "2022-01-09T20:24:50",
                                        "status": "INVALID",
                                        "invalidationRemarks": "REVOKED",
                                        "invalidatedOn": "2022-01-08T20:33:17",
                                        "recipientEmail": "riadhhossen@gmail.com",
                                        "forAuthority": "ROLE_MODERATOR",
                                        "createdBy": "syedriadhhossen",
                                        "_links": {
                                            "item": {
                                                "href": "https://localhost:8443/api/v1/invitations?id=bf81645ba06cef85",
                                                "title": "Get invitation",
                                                "type": "GET"
                                            },
                                            "create-form": {
                                                "href": "https://localhost:8443/api/v1/invitations/",
                                                "title": "Create invitation",
                                                "type": "POST"
                                            }
                                        }
                                    }
                                ],
                                "page": {
                                    "size": 1,
                                    "totalElements": 3,
                                    "totalPages": 3,
                                    "number": 2
                                }
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 403, message = "Forbidden", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "timestamp": "2022-01-14T22:10:30.731+00:00",
                                "status": 403,
                                "error": "Forbidden",
                                "message": "You do not have permission to access this resource",
                                "path": "/api/v1/invitations/"
                            }
                            """)
            }))
    })
    @Operation(method = "GET", summary = "Get invitations", description = "Returns paged list of sortable invitation elements",
            parameters = {@Parameter(name = "page"), @Parameter(name = "size"), @Parameter(name = "sort")})
    @Cacheable(value = "invitationCache")
    @GetMapping("/")
    public ResponseEntity<?> invitations(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "0") int page,
                                         @RequestParam(value = "size", defaultValue = "10") int size,
                                         @RequestParam(value = "sort", defaultValue = "id,asc") String... sort) {
        List<Sort.Order> sortOrders = new ArrayList<>();
        sortOrders.add(new Sort.Order(Sort.Direction.fromOptionalString(sort[1]).orElse(Sort.Direction.ASC), sort[0]));

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortOrders));

        Page<SignupInvitationModel> invitationPage = invitationService.invitations(pageable);

        if (invitationPage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        PageInfo pageInfo = new PageInfo(invitationPage);

        List<EntityModel<SignupInvitationModel>> invitationEntityModels = new ArrayList<>();

        for (int i = 0; i < invitationPage.getContent().size(); i += 1) {
            if (invitationService.invitation(invitationPage.getContent().get(i).getId()).isPresent()) {
                SignupInvitationModel invitation = invitationService.invitation(invitationPage.getContent().get(i).getId()).get();

                EntityModel<SignupInvitationModel> invitationEntityModel = EntityModel.of(invitation);

                invitationEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).invitation(request, invitation.getId()))
                        .withRel(IanaLinkRelations.ITEM).withTitle("Get invitation")
                        .withType(HttpMethod.GET.toString()));

                invitationEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).create(request, new SignupInvitationModel()))
                        .withRel(IanaLinkRelations.CREATE_FORM).withTitle("Create invitation")
                        .withType(HttpMethod.POST.toString()));

                if (invitation.getStatus().equals(SignupInvitationStatus.VALID)) {
                    invitationEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).invalidate(request, invitation.getId()))
                            .withRel("invalidate").withTitle("Invalidate invitation")
                            .withType(HttpMethod.PATCH.toString()));
                }

                invitationEntityModels.add(invitationEntityModel);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("invitations", invitationEntityModels);
        response.put("page", pageInfo);

        EntityModel<?> responseModel = EntityModel.of(response);

        responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).invitations(request, page, size,
                        SignupInvitationModel.class.getDeclaredFields()[1].getName(), sort[1]))
                .withRel(IanaLinkRelations.SELF).withTitle("Current Page")
                .withType(HttpMethod.GET.toString()));

        if (page > 0 && page <= (invitationPage.getTotalPages() - 1) && invitationPage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).invitations(request, 0, size,
                            SignupInvitationModel.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.FIRST).withTitle("First Page").withType(HttpMethod.GET.toString()));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).invitations(request, page - 1, size,
                            SignupInvitationModel.class.getDeclaredFields()[1].getName(), Sort.Direction.ASC.toString().toLowerCase()))
                    .withRel(IanaLinkRelations.PREVIOUS).withTitle("Previous Page").withType(HttpMethod.GET.toString()));
        }

        if (page < (invitationPage.getTotalPages() - 1) && invitationPage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).invitations(request, page + 1, size,
                            SignupInvitationModel.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.NEXT).withTitle("Next Page").withType(HttpMethod.GET.toString()));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).invitations(request, invitationPage.getTotalPages() - 1, size,
                            SignupInvitationModel.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.LAST).withTitle("Last Page").withType(HttpMethod.GET.toString()));
        }

        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "id": "bf81645ba06cef85",
                                "createdOn": "2022-01-08T20:24:50",
                                "expiresOn": "2022-01-09T20:24:50",
                                "status": "INVALID",
                                "invalidationRemarks": "REVOKED",
                                "invalidatedOn": "2022-01-08T20:33:17",
                                "recipientEmail": "riadhhossen@gmail.com",
                                "forAuthority": "ROLE_MODERATOR",
                                "createdBy": "syedriadhhossen",
                                "_links": {
                                    "invitations": {
                                        "href": "https://localhost:8443/api/v1/invitations/?page=0&size=10&sort=id,asc",
                                        "title": "Get invitations",
                                        "type": "GET"
                                    },
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/invitations?id=bf81645ba06cef85",
                                        "title": "Get invitation",
                                        "type": "GET"
                                    },
                                    "create-form": {
                                        "href": "https://localhost:8443/api/v1/invitations/",
                                        "title": "Create invitation",
                                        "type": "POST"
                                    }
                                }
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 403, message = "Forbidden", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "timestamp": "2022-01-14T22:10:30.731+00:00",
                                "status": 403,
                                "error": "Forbidden",
                                "message": "You do not have permission to access this resource",
                                "path": "/api/v1/invitations/"
                            }
                            """)
            }))
    })
    @Operation(method = "GET", summary = "Get invitation by ID",
            description = "Returns an invitation by given ID", parameters = {@Parameter(name = "id", required = true)})
    @Cacheable(value = "invitationCache", key = "#id")
    @GetMapping(params = {"id"})
    public ResponseEntity<?> invitation(HttpServletRequest request, @RequestParam("id") String id) {
        Optional<SignupInvitationModel> invitation = invitationService.invitation(id);

        if (invitation.isPresent()) {
            EntityModel<SignupInvitationModel> invitationEntityModel = EntityModel.of(invitation.get());

            invitationEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).invitations(request, 0, defaultPageSize,
                            SignupInvitationModel.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                    .withRel("invitations").withTitle("Get invitations").withType(HttpMethod.GET.toString()));

            invitationEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).invitation(request, invitation.get().getId()))
                    .withRel(IanaLinkRelations.SELF).withTitle("Get invitation").withType(HttpMethod.GET.toString()));

            invitationEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).create(request, new SignupInvitationModel()))
                    .withRel(IanaLinkRelations.CREATE_FORM).withTitle("Create invitation").withType(HttpMethod.POST.toString()));

            if (invitation.get().getStatus().equals(SignupInvitationStatus.VALID)) {
                invitationEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).invalidate(request, invitation.get().getId()))
                        .withRel("invalidate").withTitle("Invalidate invitation").withType(HttpMethod.PATCH.toString()));
            }

            return new ResponseEntity<>(invitationEntityModel, HttpStatus.OK);
        }

        throw new ResourceCrudException("Invitation not found with id: " + id, HttpStatus.NO_CONTENT, request.getRequestURI());
    }

    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "id": "bf81645ba06cef85",
                                "createdOn": "2022-01-08T20:24:50",
                                "expiresOn": "2022-01-09T20:24:50",
                                "status": "VALID",
                                "invalidationRemarks": null,
                                "invalidatedOn": null,
                                "recipientEmail": "riadhhossen@gmail.com",
                                "forAuthority": "ROLE_MODERATOR",
                                "createdBy": "syedriadhhossen",
                                "_links": {
                                    "invitations": {
                                        "href": "https://localhost:8443/api/v1/invitations/?page=0&size=10&sort=id,asc",
                                        "title": "Get invitations",
                                        "type": "GET"
                                    },
                                    "item": {
                                        "href": "https://localhost:8443/api/v1/invitations?id=bf81645ba06cef85",
                                        "title": "Get invitation",
                                        "type": "GET"
                                    },
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/invitations/",
                                        "title": "Create invitation",
                                        "type": "POST"
                                    },
                                    "invalidate": {
                                        "href": "https://localhost:8443/api/v1/invitations/bf81645ba06cef85",
                                        "title": "Invalidate invitation",
                                        "type": "POST"
                                    }
                                }
                            }
                            """)
            })),
            @ApiResponse(code = 500, message = "Internal Server Error", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "status": 500,
                                "timestamp": "31-12-2021 12:59:00",
                                "message": "Failed to create invitation",
                                "error": "Internal Server Error",
                                "path": "/api/v1/invitations/"
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
                                "path": "/api/v1/invitations/"
                            }
                            """)
            })),
            @ApiResponse(code = 400, message = "Bad Request", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "timestamp": "2022-01-14T22:10:30.731+00:00",
                                "status": 400,
                                "error": "Bad Request",
                                "message": "Valid invitation for recipient already exists",
                                "path": "/api/v1/invitations/"
                            }
                            """),
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "timestamp": "2022-01-14T22:10:30.731+00:00",
                                "status": 400,
                                "error": "Bad Request",
                                "message": "Email associated with an account",
                                "path": "/api/v1/invitations/"
                            }
                            """)
            }))
    })
    @Operation(method = "POST", summary = "Create invitation", description = "Creates and returns an invitation using the given mandatory fields")
    @Cacheable(value = "invitationCache")
    @PostMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> create(HttpServletRequest request, @RequestBody SignupInvitationModel signupInvitationModel) {
        Optional<SignupInvitationModel> invitationModel = this.invitationService.createInvitation(signupInvitationModel);

        if (invitationModel.isPresent()) {
            EntityModel<SignupInvitationModel> invitationEntityModel = EntityModel.of(invitationModel.get());

            invitationEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).invitations(request, 0, defaultPageSize,
                            SignupInvitationModel.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                    .withRel("invitations").withTitle("Get invitations").withType(HttpMethod.GET.toString()));

            invitationEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).invitation(request, invitationModel.get().getId()))
                    .withRel(IanaLinkRelations.ITEM).withTitle("Get invitation").withType(HttpMethod.GET.toString()));

            invitationEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).create(request, new SignupInvitationModel()))
                    .withRel(IanaLinkRelations.SELF).withType(HttpMethod.POST.toString()).withTitle("Create invitation"));

            invitationEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).invalidate(request, invitationModel.get().getId()))
                    .withRel("invalidate").withType(HttpMethod.PATCH.toString()).withTitle("Invalidate invitation"));

            return new ResponseEntity<>(invitationEntityModel, HttpStatus.CREATED);
        }

        throw new ResourceCrudException("Failed to create invitation", HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI());
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "id": "bf81645ba06cef85",
                                "createdOn": "2022-01-08T20:24:50",
                                "expiresOn": "2022-01-09T20:24:50",
                                "status": "VALID",
                                "invalidationRemarks": null,
                                "invalidatedOn": null,
                                "recipientEmail": "riadhhossen@gmail.com",
                                "forAuthority": "ROLE_MODERATOR",
                                "createdBy": "syedriadhhossen",
                                "_links": {
                                    "invitations": {
                                        "href": "https://localhost:8443/api/v1/invitations/?page=0&size=10&sort=id,asc",
                                        "title": "Get invitations",
                                        "type": "GET"
                                    },
                                    "item": {
                                        "href": "https://localhost:8443/api/v1/invitations?id=bf81645ba06cef85",
                                        "title": "Get invitation",
                                        "type": "GET"
                                    },
                                    "create-form": {
                                        "href": "https://localhost:8443/api/v1/invitations/",
                                        "title": "Create invitation",
                                        "type": "POST"
                                    }
                                }
                            }
                            """)
            })),
            @ApiResponse(code = 400, message = "Bad Request", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "status": 400,
                                "timestamp": "31-12-2021 12:59:00",
                                "message": "Invitation not found with id: bf81645ba06cef85",
                                "error": "Bad Request",
                                "path": "/api/v1/invitations/"
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
                                "path": "/api/v1/invitations/"
                            }
                            """)
            }))
    })
    @Operation(method = "PATCH", summary = "Invalidate invitation", description = "Invalidates and returns an invitation using the ID")
    @CachePut(value = "invitationCache", key = "#id")
    @PatchMapping(value = "/invalidate/{id}")
    public ResponseEntity<?> invalidate(HttpServletRequest request, @PathVariable("id") String id) {
        Optional<SignupInvitationModel> invitationModel = this.invitationService.invalidateInvitation(id);

        if (invitationModel.isPresent()) {
            EntityModel<SignupInvitationModel> invitationEntityModel = EntityModel.of(invitationModel.get());

            invitationEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).invitations(request, 0, defaultPageSize,
                            SignupInvitationModel.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                    .withRel("invitations").withTitle("Get invitations").withType(HttpMethod.GET.toString()));

            invitationEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).invitation(request, invitationModel.get().getId()))
                    .withRel(IanaLinkRelations.ITEM).withTitle("Get invitation").withType(HttpMethod.GET.toString()));

            invitationEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).create(request, new SignupInvitationModel()))
                    .withRel(IanaLinkRelations.CREATE_FORM).withType(HttpMethod.POST.toString()).withTitle("Create invitation"));

            return new ResponseEntity<>(invitationEntityModel, HttpStatus.OK);
        }

        throw new ResourceCrudException("Invitation not found with id: " + id, HttpStatus.BAD_REQUEST, request.getRequestURI());
    }
}
