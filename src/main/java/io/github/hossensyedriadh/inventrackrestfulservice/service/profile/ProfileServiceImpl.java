package io.github.hossensyedriadh.inventrackrestfulservice.service.profile;

import io.github.hossensyedriadh.inventrackrestfulservice.configuration.cloud.OracleCloudObjectStorage;
import io.github.hossensyedriadh.inventrackrestfulservice.entity.Profile;
import io.github.hossensyedriadh.inventrackrestfulservice.entity.User;
import io.github.hossensyedriadh.inventrackrestfulservice.model.PasswordChangeRequest;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.ProfileRepository;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.UserRepository;
import io.github.hossensyedriadh.inventrackrestfulservice.service.CurrentAuthenticationContext;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j
@Service
public class ProfileServiceImpl implements ProfileService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final CurrentAuthenticationContext currentAuthenticationContext;
    private final OracleCloudObjectStorage oracleCloudObjectStorage;
    private final ExecutorService executorService;

    @Autowired
    public ProfileServiceImpl(UserRepository userRepository, ProfileRepository profileRepository,
                              PasswordEncoder passwordEncoder, JavaMailSender javaMailSender,
                              SpringTemplateEngine templateEngine, CurrentAuthenticationContext currentAuthenticationContext,
                              OracleCloudObjectStorage oracleCloudObjectStorage) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.currentAuthenticationContext = currentAuthenticationContext;
        this.oracleCloudObjectStorage = oracleCloudObjectStorage;

        this.executorService = Executors.newFixedThreadPool(3);
    }

    @Value("${spring.mail.default-encoding}")
    private String charset;

    @Value("${accounts.mail.from}")
    private String mailFrom;

    private static final String avatarsFolderName = "avatars";

    private Boolean doPasswordsMatch(String password) {
        User user = this.currentAuthenticationContext.getAuthenticatedUser();
        return this.passwordEncoder.matches(password, user.getPassword());
    }

    @Override
    public User getUser() {
        return this.currentAuthenticationContext.getAuthenticatedUser();
    }

    @Override
    public Boolean isPasswordValid(String password) {
        return this.doPasswordsMatch(password);
    }

    @Override
    public User updateProfile(User user) {
        String previousEmail = user.getProfile().getEmail();

        profileRepository.saveAndFlush(user.getProfile());

        User updatedUser = this.currentAuthenticationContext.getAuthenticatedUser();

        if (!previousEmail.equals(updatedUser.getProfile().getEmail())) {
            this.sendEmailUpdateNotification(updatedUser.getUsername(), previousEmail, updatedUser.getProfile().getEmail());
        }

        return updatedUser;
    }

    @Async
    protected void sendEmailUpdateNotification(String username, String previousEmail, String updatedEmail) {
        this.executorService.submit(() -> {
            Context context = new Context(Locale.ENGLISH);

            Map<String, Object> mailVariables = new HashMap<>();
            mailVariables.put("datetime", LocalDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a")));
            mailVariables.put("username", username);
            mailVariables.put("updatedEmail", updatedEmail);
            context.setVariables(mailVariables);

            try {
                MimeMessage message = this.javaMailSender.createMimeMessage();
                MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, this.charset);
                messageHelper.setSubject("Email updated - InvenTrack");
                messageHelper.setFrom(this.mailFrom);
                messageHelper.setTo(previousEmail);

                final String htmlContent = this.templateEngine.process("mail/email-update-old-address.html", context);
                messageHelper.setText(htmlContent, true);
                this.javaMailSender.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
                log.error("Failed to send email", e);
            }

            try {
                MimeMessage message = this.javaMailSender.createMimeMessage();
                MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, this.charset);
                messageHelper.setSubject("Email updated - InvenTrack");
                messageHelper.setFrom(this.mailFrom);
                messageHelper.setTo(updatedEmail);

                final String htmlContent = this.templateEngine.process("mail/email-update-new-address.html", context);
                messageHelper.setText(htmlContent, true);
                this.javaMailSender.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
                log.error("Failed to send email", e);
            }
        });
    }

    @Override
    public void changePassword(PasswordChangeRequest passwordChangeRequest) {
        User currentUser = this.currentAuthenticationContext.getAuthenticatedUser();

        if (this.doPasswordsMatch(passwordChangeRequest.getCurrentPassword())) {
            String encodedPassword = this.passwordEncoder.encode(passwordChangeRequest.getNewPassword());
            currentUser.setPassword(encodedPassword);
            userRepository.saveAndFlush(currentUser);

            this.sendPasswordChangeNotification(currentUser.getUsername(), currentUser.getProfile().getEmail());
        }
    }

    @Async
    protected void sendPasswordChangeNotification(String username, String email) {
        this.executorService.submit(() -> {
            Context context = new Context(Locale.ENGLISH);

            Map<String, Object> mailVariables = new HashMap<>();
            mailVariables.put("datetime", LocalDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a")));
            mailVariables.put("username", username);
            context.setVariables(mailVariables);

            try {
                MimeMessage message = this.javaMailSender.createMimeMessage();
                MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, this.charset);
                messageHelper.setSubject("Password changed - InvenTrack");
                messageHelper.setFrom(this.mailFrom);
                messageHelper.setTo(email);

                final String htmlContent = this.templateEngine.process("mail/password-change.html", context);
                messageHelper.setText(htmlContent, true);
                this.javaMailSender.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
                log.error("Failed to send email", e);
            }
        });
    }

    @Override
    public void changeAvatar(MultipartFile file) {
        User currentUser = this.currentAuthenticationContext.getAuthenticatedUser();
        HashMap<String, String> metadata = new HashMap<>();
        metadata.put("Username", currentUser.getUsername());

        String staticUrl = this.oracleCloudObjectStorage.uploadFile(avatarsFolderName, currentUser.getUsername(), metadata, file);

        Profile profile = currentUser.getProfile();
        profile.setAvatar(staticUrl);
        profileRepository.saveAndFlush(profile);
    }
}
