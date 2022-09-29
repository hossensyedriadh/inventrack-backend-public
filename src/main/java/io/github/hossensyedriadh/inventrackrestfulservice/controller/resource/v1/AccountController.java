package io.github.hossensyedriadh.inventrackrestfulservice.controller.resource.v1;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.User;
import io.github.hossensyedriadh.inventrackrestfulservice.exception.ResourceException;
import io.github.hossensyedriadh.inventrackrestfulservice.model.PasswordBody;
import io.github.hossensyedriadh.inventrackrestfulservice.model.PasswordChangeRequest;
import io.github.hossensyedriadh.inventrackrestfulservice.service.profile.ProfileService;
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
import javax.validation.Valid;
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

    @GetMapping("/who-am-i")
    public ResponseEntity<?> profile() {
        User user = profileService.getUser();
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping(value = "/check-password", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> checkPasswordValidity(@Valid @RequestBody PasswordBody passwordBody) {
        Boolean isValid = profileService.isPasswordValid(passwordBody.getPassword());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("is_valid", isValid);

        return new ResponseEntity<>(jsonObject.toJSONString(), HttpStatus.OK);
    }

    @PatchMapping(value = "/update-profile", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> updateProfile(@Valid @RequestBody User user) {
        User updatedUser = profileService.updateProfile(user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PatchMapping(value = "/change-password", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordChangeRequest passwordChangeRequest) {
        profileService.changePassword(passwordChangeRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/change-avatar")
    public ResponseEntity<?> changeAvatar(HttpServletRequest request, @RequestPart("avatar") MultipartFile file) {
        if (!file.isEmpty()) {
            long size = file.getSize() / (1024L * 1024L);
            if (size <= 5) {
                String type = Objects.requireNonNull(file.getContentType()).toLowerCase();
                if (type.equals("image/png") || type.equals("image/jpg") || type.equals("image/jpeg")) {
                    this.profileService.changeAvatar(file);

                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);

                } else {
                    throw new ResourceException("Only PNG / JPG / JPEG files are allowed", HttpStatus.NOT_ACCEPTABLE, request);
                }
            } else {
                throw new ResourceException("Maximum allowed file size is 5 MB", HttpStatus.NOT_ACCEPTABLE, request);
            }
        } else {
            throw new ResourceException("File can not be empty", HttpStatus.NOT_ACCEPTABLE, request);
        }
    }
}
