package io.github.hossensyedriadh.InvenTrackRESTfulService.controller.resource.v1;

import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.GenericStatus;
import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.ResourceCrudException;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.NewPasswordBody;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.PasswordResetRequest;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.SignupRequest;
import io.github.hossensyedriadh.InvenTrackRESTfulService.service.open.OpenService;
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

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/v1/public", produces = {MediaType.APPLICATION_JSON_VALUE})
public class PublicController {
    private final OpenService openService;

    @Autowired
    public PublicController(OpenService openService) {
        this.openService = openService;
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
            @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                    {
                        "is_unique": true
                    }
                    """),
            @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                    {
                        "is_unique": false
                    }
                    """)
    }))
    @Operation(method = "GET", summary = "Check if username is unique",
            description = "Checks if given username is unique and returns boolean result")
    @GetMapping(value = "/check-username/{username}")
    public ResponseEntity<?> checkUsernameUniqueness(@PathVariable("username") String username) {
        Boolean isUnique = openService.isUsernameUnique(username);

        JSONObject response = new JSONObject();
        response.put("is_unique", isUnique);

        return new ResponseEntity<>(response.toJSONString(), HttpStatus.OK);
    }

    @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
            @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                    {
                        "is_unique": true
                    }
                    """),
            @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                    {
                        "is_unique": false
                    }
                    """)
    }))
    @Operation(method = "GET", summary = "Check if email is unique",
            description = "Checks if given email is unique and returns boolean result")
    @GetMapping(value = "/check-email/{email}")
    public ResponseEntity<?> checkEmailUniqueness(@PathVariable("email") String email) {
        Boolean isUnique = openService.isEmailUnique(email);

        JSONObject response = new JSONObject();
        response.put("is_unique", isUnique);

        return new ResponseEntity<>(response.toJSONString(), HttpStatus.OK);
    }

    @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
            @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                    {
                        "is_valid": true
                    }
                    """),
            @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                    {
                        "is_valid": false
                    }
                    """)
    }))
    @Operation(method = "GET", summary = "Check if invitation token is valid",
            description = "Checks if given token is valid and returns boolean result")
    @GetMapping(value = "/invited/{token}")
    public ResponseEntity<?> checkValidity(@PathVariable("token") String token) {
        Boolean result = openService.isTokenValid(token);

        JSONObject response = new JSONObject();
        response.put("is_valid", result);

        return new ResponseEntity<>(response.toJSONString(), HttpStatus.OK);

    }

    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 403, message = "Forbidden", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "status": 403,
                                "timestamp": "31-12-2021 12:59:00",
                                "message": "Invalid token",
                                "error": "Forbidden",
                                "path": "/api/v1/public/sign-up"
                            }
                            """)
            })),
            @ApiResponse(code = 500, message = "Internal Server Error", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "status": 500,
                                "timestamp": "31-12-2021 12:59:00",
                                "message": "Failed to process signup request",
                                "error": "Internal Server Error",
                                "path": "/api/v1/public/sign-up"
                            }
                            """)
            }))
    })
    @Operation(method = "POST", summary = "Request for signup", description = "Processes signup request with given information")
    @PostMapping(value = "/sign-up", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> signup(HttpServletRequest request, @RequestBody SignupRequest signupRequest) {
        GenericStatus response = openService.doSignup(signupRequest);

        if (response.equals(GenericStatus.SUCCESSFUL)) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        }

        throw new ResourceCrudException("Failed to process signup request", HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI());
    }

    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 400, message = "Bad Request", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "status": 400,
                                "timestamp": "31-12-2021 12:59:00",
                                "message": "Invalid username",
                                "error": "Bad Request",
                                "path": "/api/v1/public/who-forgot-password"
                            }
                            """)
            }))
    })
    @Operation(method = "POST", summary = "Request password reset OTP", description = "Checks username/email and sends OTP to the registered email if user exists")
    @PostMapping(value = "/who-forgot-password/{username}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> requestPasswordReset(HttpServletRequest request, @PathVariable("username") String username) {
        GenericStatus status = openService.requestPasswordReset(username);

        if (status.equals(GenericStatus.SUCCESSFUL)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            throw new ResourceCrudException("Invalid username", HttpStatus.BAD_REQUEST, request.getRequestURI());
        }
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "No Content", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "is_valid": true
                            }
                            """),
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "is_valid": false
                            }
                            """)
            })),
            @ApiResponse(code = 400, message = "Bad Request", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "status": 400,
                                "timestamp": "31-12-2021 12:59:00",
                                "message": "Invalid username",
                                "error": "Bad Request",
                                "path": "/api/v1/public/check-otp"
                            }
                            """)
            }))
    })

    @Operation(method = "POST", summary = "Checks OTP validity", description = "Checks username/email and OTP for validity")
    @PostMapping(value = "/check-otp", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> checkOtp(@RequestBody PasswordResetRequest resetRequest) {
        Boolean status = openService.checkOtp(resetRequest.getId(), resetRequest.getOtp());

        JSONObject responseObject = new JSONObject();
        responseObject.put("is_valid", status);

        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 400, message = "Bad Request", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "status": 400,
                                "timestamp": "31-12-2021 12:59:00",
                                "message": "Invalid username",
                                "error": "Bad Request",
                                "path": "/api/v1/public/reset-password"
                            }
                            """)
            })),
            @ApiResponse(code = 500, message = "Internal Server Error", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "status": 500,
                                "timestamp": "31-12-2021 12:59:00",
                                "message": "Failed to reset password",
                                "error": "Internal Server Error",
                                "path": "/api/v1/public/reset-password"
                            }
                            """)
            }))
    })
    @Operation(method = "POST", summary = "Request for password reset",
            description = "Resets password with given new password if username/email and OTP is valid")
    @PostMapping(value = "/reset-password", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> resetPassword(HttpServletRequest request, @RequestBody NewPasswordBody newPasswordBody) {
        GenericStatus response = openService.resetPassword(newPasswordBody.getId(),
                newPasswordBody.getOtp(), newPasswordBody.getNewPassword());

        if (response.equals(GenericStatus.SUCCESSFUL)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else if (response.equals(GenericStatus.FAILED)) {
            throw new ResourceCrudException("Failed to reset password", HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI());
        } else {
            throw new ResourceCrudException("Invalid username", HttpStatus.BAD_REQUEST, request.getRequestURI());
        }
    }
}
