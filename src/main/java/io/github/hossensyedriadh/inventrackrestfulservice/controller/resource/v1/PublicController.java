package io.github.hossensyedriadh.inventrackrestfulservice.controller.resource.v1;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.User;
import io.github.hossensyedriadh.inventrackrestfulservice.model.PasswordResetBody;
import io.github.hossensyedriadh.inventrackrestfulservice.model.PasswordResetRequest;
import io.github.hossensyedriadh.inventrackrestfulservice.model.SignupRequest;
import io.github.hossensyedriadh.inventrackrestfulservice.service.open.OpenService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/v1/public", produces = {MediaType.APPLICATION_JSON_VALUE})
public class PublicController {
    private final OpenService openService;

    @Autowired
    public PublicController(OpenService openService) {
        this.openService = openService;
    }

    @GetMapping("/check-username/{username}")
    public ResponseEntity<?> checkUsername(@PathVariable("username") String username) {
        Boolean isUnique = openService.isUsernameUnique(username);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("is_unique", isUnique);
        return new ResponseEntity<>(jsonObject.toJSONString(), HttpStatus.OK);
    }

    @GetMapping("/check-email/{email}")
    public ResponseEntity<?> checkEmail(@PathVariable("email") String email) {
        Boolean isUnique = openService.isEmailUnique(email);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("is_unique", isUnique);
        return new ResponseEntity<>(jsonObject.toJSONString(), HttpStatus.OK);
    }

    @GetMapping("/invited/{token}")
    public ResponseEntity<?> checkInvitationTokenValidity(@PathVariable("token") String token) {
        Boolean result = openService.isInvitationTokenValid(token);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("is_valid", result);
        return new ResponseEntity<>(jsonObject.toJSONString(), HttpStatus.OK);
    }

    @PostMapping(value = "/signup", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest) {
        User user = openService.signUp(signupRequest);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/who-forgot-password/{username}")
    public ResponseEntity<?> requestPasswordReset(@PathVariable("username") String username) {
        openService.requestPasswordReset(username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/check-otp", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> checkOtp(@Valid @RequestBody PasswordResetRequest resetRequest) {
        Boolean status = openService.checkOtp(resetRequest);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("is_valid", status);
        return new ResponseEntity<>(jsonObject.toJSONString(), HttpStatus.OK);
    }

    @PostMapping(value = "/reset-password", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetBody passwordResetBody) {
        openService.resetPassword(passwordResetBody);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
