package io.github.hossensyedriadh.InvenTrackRESTfulService.service.open;

import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.Profile;
import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.SavedCode;
import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.SignupInvitation;
import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.User;
import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.*;
import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.ResourceCrudException;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.SignupRequest;
import io.github.hossensyedriadh.InvenTrackRESTfulService.repository.SavedCodeRepository;
import io.github.hossensyedriadh.InvenTrackRESTfulService.repository.SignupInvitationRepository;
import io.github.hossensyedriadh.InvenTrackRESTfulService.repository.UserRepository;
import io.github.hossensyedriadh.InvenTrackRESTfulService.utils.Generator;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public final class OpenServiceImpl implements OpenService {
    private final ObjectFactory<UserRepository> userRepositoryObjectFactory;
    private final ObjectFactory<SavedCodeRepository> savedCodeRepositoryObjectFactory;
    private final ObjectFactory<SignupInvitationRepository> signupInvitationRepositoryObjectFactory;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final HttpServletRequest httpServletRequest;

    @Value("${spring.mail.default-encoding}")
    private String charset;

    @Value("${accounts.mail.from}")
    private String mailFrom;

    @Autowired
    public OpenServiceImpl(ObjectFactory<UserRepository> userRepositoryObjectFactory,
                           ObjectFactory<SavedCodeRepository> savedCodeRepositoryObjectFactory,
                           ObjectFactory<SignupInvitationRepository> signupInvitationRepositoryObjectFactory,
                           PasswordEncoder passwordEncoder, JavaMailSender mailSender,
                           TemplateEngine templateEngine, HttpServletRequest httpServletRequest) {
        this.userRepositoryObjectFactory = userRepositoryObjectFactory;
        this.savedCodeRepositoryObjectFactory = savedCodeRepositoryObjectFactory;
        this.signupInvitationRepositoryObjectFactory = signupInvitationRepositoryObjectFactory;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public Boolean isUsernameUnique(String username) {
        return userRepositoryObjectFactory.getObject().findById(username).isEmpty();
    }

    @Override
    public Boolean isEmailUnique(String email) {
        List<User> users = userRepositoryObjectFactory.getObject().findAll()
                .stream().filter(usr -> usr.getProfile().getEmail().equals(email)).toList();

        return !(users.size() > 0);
    }

    @Override
    public Boolean isTokenValid(String token) {
        List<SignupInvitation> invitations = signupInvitationRepositoryObjectFactory.getObject().findAll().stream()
                .filter(signupInvitation -> signupInvitation.getToken().equals(token)).toList();

        if (invitations.size() == 1) {
            SignupInvitation invitation = invitations.get(0);
            return invitation != null && invitation.getStatus().equals(SignupInvitationStatus.VALID)
                    && invitation.getExpiresOn().isAfter(LocalDateTime.now());
        }

        return false;
    }

    @Override
    public GenericStatus doSignup(SignupRequest signupRequest) {
        if (this.isTokenValid(signupRequest.getToken())) {
            SignupInvitation invitation = signupInvitationRepositoryObjectFactory.getObject().findAll().stream()
                    .filter(i -> i.getToken().equals(signupRequest.getToken())).toList().get(0);
            User user = new User();
            user.setUsername(signupRequest.getUsername());
            user.setPassword(passwordEncoder.encode(signupRequest.getPassphrase()));
            user.setAuthority((invitation.getForAuthority() == InvitedUserAuthority.ROLE_ADMINISTRATOR) ?
                    Authority.ROLE_ADMINISTRATOR : Authority.ROLE_MODERATOR);

            Profile profile = new Profile();
            profile.setFirstName(signupRequest.getFirstName());
            profile.setLastName(signupRequest.getLastName());
            profile.setEmail(invitation.getRecipientEmail());
            profile.setPhoneNo((signupRequest.getPhone() != null) ? signupRequest.getPhone() : null);
            profile.setAvatar((signupRequest.getAvatar() != null) ? signupRequest.getAvatar() : null);

            user.setProfile(profile);
            userRepositoryObjectFactory.getObject().saveAndFlush(user);

            if (userRepositoryObjectFactory.getObject().findById(signupRequest.getUsername()).isPresent()) {
                this.sendSignupSuccessNotification(signupRequest.getUsername(), invitation.getRecipientEmail());
                this.invalidateInvitation(signupRequest.getToken());

                return GenericStatus.SUCCESSFUL;
            }

            return GenericStatus.FAILED;
        }

        throw new ResourceCrudException("Invalid token", HttpStatus.FORBIDDEN, httpServletRequest.getRequestURI());
    }

    private void invalidateInvitation(String token) {
        List<SignupInvitation> invitations = signupInvitationRepositoryObjectFactory.getObject().findAll()
                .stream().filter(invitation -> invitation.getToken().equals(token)).toList();

        if (invitations.size() == 1) {
            SignupInvitation invitation = invitations.get(0);
            invitation.setStatus(SignupInvitationStatus.INVALID);
            invitation.setInvalidationRemarks(SignupInvitationInvalidationRemarks.USED);
            invitation.setInvalidatedOn(LocalDateTime.now());
            signupInvitationRepositoryObjectFactory.getObject().saveAndFlush(invitation);
        }
    }

    private void sendSignupSuccessNotification(String username, String email) {
        Context context = new Context(Locale.ENGLISH);

        Map<String, Object> mailVariables = new HashMap<>();
        mailVariables.put("username", username);
        context.setVariables(mailVariables);

        try {
            MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, this.charset);
            mimeMessageHelper.setSubject("Welcome to InvenTrack");
            mimeMessageHelper.setFrom(this.mailFrom);
            mimeMessageHelper.setTo(email);

            final String htmlContent = this.templateEngine.process("mail/welcome.html", context);
            mimeMessageHelper.setText(htmlContent, true);

            Thread smpThread = new Thread(() -> mailSender.send(mimeMessage));
            smpThread.start();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GenericStatus requestPasswordReset(String username) {
        if (userRepositoryObjectFactory.getObject().findById(username).isPresent()) {
            User user = userRepositoryObjectFactory.getObject().findById(username).get();

            String code = Generator.generateRandomString(8, true);

            SavedCode savedCode = new SavedCode();
            savedCode.setCode(code);
            savedCode.setForUser(user);
            String id = savedCodeRepositoryObjectFactory.getObject().saveAndFlush(savedCode).getId();

            ScheduledThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(20);
            threadPoolExecutor.schedule(() -> {
                if (savedCodeRepositoryObjectFactory.getObject().existsById(id)) {
                    savedCodeRepositoryObjectFactory.getObject().deleteById(id);
                }
            }, 10, TimeUnit.MINUTES);
            threadPoolExecutor.setKeepAliveTime(15, TimeUnit.MINUTES);
            threadPoolExecutor.allowCoreThreadTimeOut(true);

            this.sendPasswordResetOtp(user.getUsername(), user.getProfile().getEmail(), code);

            return GenericStatus.SUCCESSFUL;
        } else if (userRepositoryObjectFactory.getObject().findAll()
                .stream().filter(user -> user.getProfile().getEmail().equals(username)).toList().size() == 1) {
            User user = userRepositoryObjectFactory.getObject().findAll()
                    .stream().filter(usr -> usr.getProfile().getEmail().equals(username)).toList().get(0);

            String code = Generator.generateRandomString(8, true);

            SavedCode savedCode = new SavedCode();
            savedCode.setCode(code);
            savedCode.setForUser(user);
            String id = savedCodeRepositoryObjectFactory.getObject().saveAndFlush(savedCode).getId();

            ScheduledThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(20);
            threadPoolExecutor.schedule(() -> {
                if (savedCodeRepositoryObjectFactory.getObject().existsById(id)) {
                    savedCodeRepositoryObjectFactory.getObject().deleteById(id);
                }
            }, 10, TimeUnit.MINUTES);
            threadPoolExecutor.setKeepAliveTime(15, TimeUnit.MINUTES);
            threadPoolExecutor.allowCoreThreadTimeOut(true);

            this.sendPasswordResetOtp(user.getUsername(), user.getProfile().getEmail(), code);

            return GenericStatus.SUCCESSFUL;
        } else {
            return GenericStatus.FAILED;
        }
    }

    private void sendPasswordResetOtp(String username, String email, String code) {
        Context context = new Context(Locale.ENGLISH);
        String[] localDateTime = LocalDateTime.now().toString().split("T");

        Map<String, Object> mailVariables = new HashMap<>();
        mailVariables.put("datetime", localDateTime[0].concat(" ").concat(localDateTime[1].split("\\.")[0]));
        mailVariables.put("username", username);
        mailVariables.put("code", code);

        context.setVariables(mailVariables);

        try {
            MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, this.charset);
            mimeMessageHelper.setSubject("Password reset - InvenTrack");
            mimeMessageHelper.setFrom(this.mailFrom);
            mimeMessageHelper.setTo(email);

            final String htmlContent = this.templateEngine.process("mail/password-reset-code.html", context);
            mimeMessageHelper.setText(htmlContent, true);

            Thread smpThread = new Thread(() -> mailSender.send(mimeMessage));
            smpThread.start();
        } catch (MessagingException e) {
            throw new ResourceCrudException((e.getCause() != null) ? e.getCause().getMessage() : e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR, httpServletRequest.getRequestURI());
        }
    }

    @Override
    public Boolean checkOtp(String username, String otp) {
        if (userRepositoryObjectFactory.getObject().findById(username).isPresent()) {
            List<SavedCode> savedCodes = savedCodeRepositoryObjectFactory.getObject().findAll()
                    .stream().filter(code -> code.getCode().equals(otp)
                            && code.getForUser().getUsername().equals(username)).toList();

            return savedCodes.size() == 1;
        } else if (userRepositoryObjectFactory.getObject().findAll().stream()
                .filter(user -> user.getProfile().getEmail().equals(username)).toList().size() == 1) {
            List<SavedCode> savedCodes = savedCodeRepositoryObjectFactory.getObject().findAll()
                    .stream().filter(code -> code.getCode().equals(otp)
                            && code.getForUser().getProfile().getEmail().equals(username)).toList();

            return savedCodes.size() == 1;
        }

        throw new ResourceCrudException("Invalid username", HttpStatus.BAD_REQUEST, httpServletRequest.getRequestURI());
    }

    @Override
    public GenericStatus resetPassword(String id, String otp, String newPassword) {
        if (userRepositoryObjectFactory.getObject().findById(id).isPresent()) {
            User user = userRepositoryObjectFactory.getObject().findById(id).get();

            List<SavedCode> savedCodes = savedCodeRepositoryObjectFactory.getObject().findAll().stream()
                    .filter(code -> code.getCode().equals(otp)
                            && code.getForUser().getUsername().equals(user.getUsername())).toList();

            if (savedCodes.size() == 1) {
                String encodedNewPassword = passwordEncoder.encode(newPassword);
                user.setPassword(encodedNewPassword);

                User savedUser = userRepositoryObjectFactory.getObject().saveAndFlush(user);

                if (savedUser.getPassword().equals(encodedNewPassword)) {
                    SavedCode savedCode = savedCodes.get(0);
                    savedCodeRepositoryObjectFactory.getObject().deleteById(savedCode.getId());

                    this.sendPasswordResetNotification(savedUser.getUsername(), savedUser.getProfile().getEmail());

                    return GenericStatus.SUCCESSFUL;
                }
                return GenericStatus.FAILED;
            }
            return GenericStatus.FAILED;
        } else if (userRepositoryObjectFactory.getObject().findAll()
                .stream().filter(user -> user.getProfile().getEmail().equals(id)).toList().size() == 1) {
            User user = userRepositoryObjectFactory.getObject().findAll()
                    .stream().filter(usr -> usr.getProfile().getEmail().equals(id)).toList().get(0);

            List<SavedCode> savedCodes = savedCodeRepositoryObjectFactory.getObject().findAll().stream()
                    .filter(code -> code.getCode().equals(otp)
                            && code.getForUser().getUsername().equals(user.getUsername())).toList();

            if (savedCodes.size() == 1) {
                String encodedNewPassword = passwordEncoder.encode(newPassword);
                user.setPassword(encodedNewPassword);

                User savedUser = userRepositoryObjectFactory.getObject().saveAndFlush(user);

                if (savedUser.getPassword().equals(encodedNewPassword)) {
                    SavedCode savedCode = savedCodes.get(0);
                    savedCodeRepositoryObjectFactory.getObject().deleteById(savedCode.getId());

                    this.sendPasswordResetNotification(savedUser.getUsername(), savedUser.getProfile().getEmail());

                    return GenericStatus.SUCCESSFUL;
                }
            }
            return GenericStatus.FAILED;
        }

        throw new ResourceCrudException("Invalid username", HttpStatus.BAD_REQUEST, httpServletRequest.getRequestURI());
    }

    private void sendPasswordResetNotification(String username, String email) {
        Context context = new Context(Locale.ENGLISH);
        String[] localDateTime = LocalDateTime.now().toString().split("T");

        Map<String, Object> mailVariables = new HashMap<>();
        mailVariables.put("datetime", localDateTime[0].concat(" ").concat(localDateTime[1].split("\\.")[0]));
        mailVariables.put("username", username);

        context.setVariables(mailVariables);

        try {
            MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, this.charset);
            mimeMessageHelper.setSubject("Password reset successful - InvenTrack");
            mimeMessageHelper.setFrom(this.mailFrom);
            mimeMessageHelper.setTo(email);

            final String htmlContent = this.templateEngine.process("mail/password-reset.html", context);
            mimeMessageHelper.setText(htmlContent, true);

            Thread smpThread = new Thread(() -> mailSender.send(mimeMessage));
            smpThread.start();
        } catch (MessagingException e) {
            throw new ResourceCrudException((e.getCause() != null) ? e.getCause().getMessage() : e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR, httpServletRequest.getRequestURI());
        }
    }
}
