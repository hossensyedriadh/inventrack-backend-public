package io.github.hossensyedriadh.InvenTrackRESTfulService.controller.resource.v1;

import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.GenericStatus;
import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.ResourceCrudException;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.PasswordBody;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.PasswordChangeRequest;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.UserModel;
import io.github.hossensyedriadh.InvenTrackRESTfulService.service.profile.ProfileService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;
import io.swagger.v3.oas.annotations.Operation;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@RestController
@RequestMapping(value = "/v1/profile", produces = {MediaType.APPLICATION_JSON_VALUE})
public class AccountController {
    private final ProfileService profileService;

    @Autowired
    public AccountController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
            @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                    {
                        "username": "john",
                        "enabled": true,
                        "accountNotLocked": true,
                        "authority": "ROLE_ADMINISTRATOR",
                        "profile": {
                            "id": "7e3f8a28-f3eb-4040-9401-51f85ecf1cf7",
                            "firstName": "John",
                            "lastName": "Doe",
                            "email": "john@test.com",
                            "phone": "+1123654926",
                            "userSince": "2021-12-30",
                            "avatar": null
                        }
                    }
                    """)
    }))
    @Operation(method = "GET", summary = "User's account information",
            description = "Returns authenticated user's account information")
    @GetMapping("/who-am-i")
    public ResponseEntity<?> profile() {
        UserModel profile = this.profileService.getUser();

        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
            @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                    {
                        "is_valid": true
                    }"""),
            @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                    {
                        "is_valid": false
                    }""")
    }))
    @Operation(method = "POST", summary = "Check password validity",
            description = "Checks a given password's validity against the currently authenticated user's account")
    @PostMapping(value = "/check-password", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> checkPasswordValidity(@RequestBody PasswordBody passwordBody) {
        Boolean isValid = this.profileService.isPasswordValid(passwordBody.getPassword());

        JSONObject responseObject = new JSONObject();
        responseObject.put("is_valid", isValid);
        return new ResponseEntity<>(responseObject.toJSONString(), HttpStatus.OK);
    }

    @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
            @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                    {
                        "username": "john",
                        "enabled": true,
                        "accountNotLocked": true,
                        "authority": "ROLE_ADMINISTRATOR",
                        "profile": {
                            "id": "7e3f8a28-f3eb-4040-9401-51f85ecf1cf7",
                            "firstName": "John",
                            "lastName": "Doe",
                            "email": "john@test.com",
                            "phone": "+1123654926",
                            "userSince": "2021-12-30",
                            "avatar": null
                        }
                    }
                    """)
    }))
    @Operation(method = "PATCH", summary = "Update profile",
            description = "Updates and returns authenticated user's account information")
    @PatchMapping(value = "/update-profile", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> updateProfile(@RequestBody UserModel userModel) {
        UserModel updatedProfile = this.profileService.updateProfile(userModel);

        return new ResponseEntity<>(updatedProfile, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 400, message = "Bad Request", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "status": 400,
                                "timestamp": "31-12-2021 12:59:00",
                                "message": "Wrong password",
                                "error": "Bad Request",
                                "path": "/api/v1/profile/change-password"
                            }
                            """)
            })),
            @ApiResponse(code = 500, message = "Internal Server Error", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "status": 500,
                                "timestamp": "31-12-2021 12:59:00",
                                "message": "Failed to change password",
                                "error": "Internal Server Error",
                                "path": "/api/v1/profile/change-password"
                            }
                            """)
            }))
    })
    @Operation(method = "PATCH", summary = "Change password", description = "Change authenticated user's password")
    @PatchMapping(value = "/change-password", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> changePassword(HttpServletRequest request, @RequestBody PasswordChangeRequest passwordChangeRequest) {
        GenericStatus result = this.profileService.changePassword(passwordChangeRequest.getCurrentPassword(),
                passwordChangeRequest.getNewPassword());

        if (result.equals(GenericStatus.SUCCESSFUL)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            throw new ResourceCrudException("Failed to change password", HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI());
        }
    }

    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 500, message = "Internal Server Error", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "status": 500,
                                "timestamp": "31-12-2021 12:59:00",
                                "message": "Failed to change avatar",
                                "error": "Internal Server Error",
                                "path": "/api/v1/profile/change-avatar"
                            }
                            """)
            }))
    })
    @Operation(method = "PATCH", summary = "Change avatar", description = "Change authenticated user's avatar")
    @PatchMapping(value = "/change-avatar")
    public ResponseEntity<?> changeAvatar(HttpServletRequest request, @RequestPart("avatar") MultipartFile file) {
        if (!file.isEmpty()) {
            long size = file.getSize() / (1024L * 1024L);
            if (size <= 5) {
                String type = Objects.requireNonNull(file.getContentType()).toLowerCase();
                if (type.equals("image/png") || type.equals("image/jpg") || type.equals("image/jpeg")) {
                    GenericStatus result = this.profileService.changeAvatar(file);

                    if (result.equals(GenericStatus.SUCCESSFUL)) {
                        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                    } else {
                        throw new ResourceCrudException("Failed to change avatar", HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI());
                    }
                } else {
                    throw new ResourceCrudException("Only PNG / JPG / JPEG files are allowed", HttpStatus.NOT_ACCEPTABLE, request.getRequestURI());
                }
            } else {
                throw new ResourceCrudException("Maximum allowed file size is 5 MB", HttpStatus.NOT_ACCEPTABLE, request.getRequestURI());
            }
        } else {
            throw new ResourceCrudException("File can not be empty", HttpStatus.NOT_ACCEPTABLE, request.getRequestURI());
        }
    }
}
