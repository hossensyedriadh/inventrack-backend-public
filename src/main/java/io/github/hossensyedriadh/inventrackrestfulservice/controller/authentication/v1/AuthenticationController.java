package io.github.hossensyedriadh.inventrackrestfulservice.controller.authentication.v1;

import io.github.hossensyedriadh.inventrackrestfulservice.authentication.bearer_authentication.bearer_token.AccessTokenRequest;
import io.github.hossensyedriadh.inventrackrestfulservice.authentication.bearer_authentication.bearer_token.BearerTokenRequest;
import io.github.hossensyedriadh.inventrackrestfulservice.authentication.bearer_authentication.bearer_token.BearerTokenResponse;
import io.github.hossensyedriadh.inventrackrestfulservice.service.authentication.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/authentication", consumes = {MediaType.APPLICATION_JSON_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE})
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @PostMapping("/")
    public ResponseEntity<?> authenticate(@RequestBody BearerTokenRequest bearerTokenRequest) {
        BearerTokenResponse bearerTokenResponse = this.authenticationService.authenticate(bearerTokenRequest);
        return new ResponseEntity<>(bearerTokenResponse, HttpStatus.OK);
    }

    @PostMapping("/access-token")
    public ResponseEntity<?> renewAccessToken(@RequestBody AccessTokenRequest accessTokenRequest) {
        BearerTokenResponse bearerTokenResponse = this.authenticationService.renewAccessToken(accessTokenRequest);
        return new ResponseEntity<>(bearerTokenResponse, HttpStatus.OK);
    }
}
