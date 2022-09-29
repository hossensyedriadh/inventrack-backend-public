package io.github.hossensyedriadh.inventrackrestfulservice.service.open;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.PersistedOtp;
import io.github.hossensyedriadh.inventrackrestfulservice.entity.Profile;
import io.github.hossensyedriadh.inventrackrestfulservice.entity.SignupInvitation;
import io.github.hossensyedriadh.inventrackrestfulservice.entity.User;
import io.github.hossensyedriadh.inventrackrestfulservice.enumerator.Authority;
import io.github.hossensyedriadh.inventrackrestfulservice.enumerator.InvitationInvalidationRemarks;
import io.github.hossensyedriadh.inventrackrestfulservice.enumerator.InvitationStatus;
import io.github.hossensyedriadh.inventrackrestfulservice.enumerator.InvitedUserAuthority;
import io.github.hossensyedriadh.inventrackrestfulservice.exception.ResourceException;
import io.github.hossensyedriadh.inventrackrestfulservice.model.PasswordResetBody;
import io.github.hossensyedriadh.inventrackrestfulservice.model.PasswordResetRequest;
import io.github.hossensyedriadh.inventrackrestfulservice.model.SignupRequest;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.PersistedOtpRepository;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.SignupInvitationRepository;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.UserRepository;
import io.github.hossensyedriadh.inventrackrestfulservice.utils.Generator;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Log4j
@Service
public class OpenServiceImpl implements OpenService {
    private final UserRepository userRepository;
    private final PersistedOtpRepository otpRepository;
    private final SignupInvitationRepository invitationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final HttpServletRequest httpServletRequest;
    private final ExecutorService executorService;

    @Autowired
    public OpenServiceImpl(UserRepository userRepository, PersistedOtpRepository otpRepository,
                           SignupInvitationRepository invitationRepository, PasswordEncoder passwordEncoder,
                           JavaMailSender javaMailSender, SpringTemplateEngine templateEngine,
                           HttpServletRequest httpServletRequest) {
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.invitationRepository = invitationRepository;
        this.passwordEncoder = passwordEncoder;
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.httpServletRequest = httpServletRequest;

        this.executorService = Executors.newSingleThreadExecutor();
    }

    @Value("${spring.mail.default-encoding}")
    private String charset;

    @Value("${accounts.mail.from}")
    private String mailFrom;

    @Override
    public Boolean isUsernameUnique(String username) {
        return userRepository.findById(username).isEmpty();
    }

    @Override
    public Boolean isEmailUnique(String email) {
        return userRepository.findAll().stream().noneMatch(user -> user.getProfile().getEmail().equals(email));
    }

    @Override
    public Boolean isInvitationTokenValid(String token) {
        List<SignupInvitation> invitations = invitationRepository.findAll().stream().filter(invitation -> invitation.getToken().equals(token)).toList();
        return !invitations.isEmpty();
    }

    @Override
    public User signUp(SignupRequest signupRequest) {
        if (this.isInvitationTokenValid(signupRequest.getInvitationToken())) {
            SignupInvitation invitation = invitationRepository.findAll().stream().filter(inv -> inv.getToken()
                    .equals(signupRequest.getInvitationToken())).toList().get(0);

            User user = new User();
            user.setPassword(this.passwordEncoder.encode(signupRequest.getPassword()));
            user.setAuthority(invitation.getForAuthority().equals(InvitedUserAuthority.ROLE_MODERATOR) ?
                    Authority.ROLE_ADMINISTRATOR : Authority.ROLE_MODERATOR);

            Profile profile = new Profile();
            profile.setFirstName(signupRequest.getFirstName());
            profile.setLastName(signupRequest.getLastName());
            profile.setEmail(invitation.getRecipientEmail());
            profile.setPhone(signupRequest.getPhone());
            user.setProfile(profile);

            User createdUser = userRepository.saveAndFlush(user);
            this.invalidateInvitation(invitation);
            this.sendSignupSuccessNotification(createdUser.getUsername(), createdUser.getProfile().getEmail());

            return createdUser;
        } else {
            throw new ResourceException("Invalid invitation token", HttpStatus.FORBIDDEN, httpServletRequest);
        }
    }

    @Async
    protected void invalidateInvitation(SignupInvitation invitation) {
        this.executorService.submit(() -> {
            invitation.setStatus(InvitationStatus.INVALID);
            invitation.setInvalidationRemarks(InvitationInvalidationRemarks.USED);
            invitation.setInvalidatedOn(LocalDateTime.now(ZoneId.systemDefault()));
        });
    }

    @Async
    protected void sendSignupSuccessNotification(String username, String email) {
        this.executorService.submit(() -> {
            Context context = new Context(Locale.ENGLISH);

            Map<String, Object> mailVariables = new HashMap<>();
            mailVariables.put("username", username);
            context.setVariables(mailVariables);

            try {
                MimeMessage message = this.javaMailSender.createMimeMessage();
                MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, this.charset);
                messageHelper.setSubject("Welcome to InvenTrack");
                messageHelper.setFrom(this.mailFrom);
                messageHelper.setTo(email);

                final String htmlContent = this.templateEngine.process("mail/welcome.html", context);
                messageHelper.setText(htmlContent, true);
                this.javaMailSender.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
                log.error("Failed to send email", e);
            }
        });
    }

    @Override
    public void requestPasswordReset(String username) {
        boolean isUsername = username.matches("^[a-zA-Z_]{4,75}$");
        boolean isEmail = username.matches("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+$");

        User user;

        if (isUsername) {
            if (userRepository.findById(username).isPresent()) {
                user = userRepository.findById(username).get();
            } else {
                throw new ResourceException("User not found: " + username, HttpStatus.BAD_REQUEST, httpServletRequest);
            }
        } else if (isEmail) {
            if (userRepository.findAll().stream().anyMatch(usr -> usr.getProfile().getEmail().equals(username))) {
                user = userRepository.findAll().stream().filter(usr -> usr.getProfile().getEmail().equals(username)).toList().get(0);
            } else {
                throw new ResourceException("User not found: " + username, HttpStatus.BAD_REQUEST, httpServletRequest);
            }
        } else {
            throw new ResourceException("Invalid username: " + username, HttpStatus.BAD_REQUEST, httpServletRequest);
        }

        String code = Generator.generateOtp(8, true);

        PersistedOtp otp = new PersistedOtp();
        otp.setCode(code);
        otp.setForUser(user);

        String id = otpRepository.saveAndFlush(otp).getId();

        this.sendPasswordResetOtp(username, user.getProfile().getEmail(), code);

        ScheduledThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(5);
        threadPoolExecutor.schedule(() -> {
            if (otpRepository.existsById(id)) {
                otpRepository.deleteById(id);
            }
        }, 10, TimeUnit.MINUTES);
        threadPoolExecutor.setKeepAliveTime(12, TimeUnit.MINUTES);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
    }

    @Async
    protected void sendPasswordResetOtp(String username, String email, String code) {
        this.executorService.submit(() -> {
            Context context = new Context(Locale.ENGLISH);

            Map<String, Object> mailVariables = new HashMap<>();
            mailVariables.put("datetime", LocalDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a")));
            mailVariables.put("username", username);
            mailVariables.put("code", code);
            context.setVariables(mailVariables);

            try {
                MimeMessage message = this.javaMailSender.createMimeMessage();
                MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, this.charset);
                messageHelper.setSubject("Password reset - InvenTrack");
                messageHelper.setFrom(this.mailFrom);
                messageHelper.setTo(email);

                final String htmlContent = this.templateEngine.process("mail/password-reset-code.html", context);
                messageHelper.setText(htmlContent, true);
                this.javaMailSender.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
                log.error("Failed to send email", e);
            }
        });
    }

    @Override
    public Boolean checkOtp(PasswordResetRequest resetRequest) {
        boolean isUsername = resetRequest.getId().matches("^[a-zA-Z_]{4,75}$");
        boolean isEmail = resetRequest.getId().matches("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+$");

        if (isUsername) {
            if (userRepository.findById(resetRequest.getId()).isPresent()) {
                List<PersistedOtp> otps = otpRepository.findAll().stream().filter(otp -> otp.getCode().equals(resetRequest.getOtp())
                                && otp.getForUser().getUsername().equals(resetRequest.getId())
                                && otp.getExpiresOn().isAfter(LocalDateTime.now(ZoneId.systemDefault())))
                        .toList();

                if (!otps.isEmpty()) {
                    otpRepository.deleteAll(otps);
                    return true;
                }
                return false;
            }

            throw new ResourceException("User not found: " + resetRequest.getId(), HttpStatus.BAD_REQUEST, httpServletRequest);
        } else if (isEmail) {
            if (userRepository.findAll().stream().anyMatch(user -> user.getProfile().getEmail().equals(resetRequest.getId()))) {
                List<PersistedOtp> otps = otpRepository.findAll().stream().filter(otp -> otp.getCode().equals(resetRequest.getOtp())
                        && otp.getForUser().getProfile().getEmail().equals(resetRequest.getId())
                        && otp.getExpiresOn().isAfter(LocalDateTime.now(ZoneId.systemDefault()))).toList();

                if (!otps.isEmpty()) {
                    otpRepository.deleteAll(otps);
                    return true;
                }

                return false;
            }

            throw new ResourceException("User not found: " + resetRequest.getId(), HttpStatus.BAD_REQUEST, httpServletRequest);
        }

        throw new ResourceException("Invalid ID: " + resetRequest.getId(), HttpStatus.BAD_REQUEST, httpServletRequest);
    }

    @Override
    public void resetPassword(PasswordResetBody passwordResetBody) {
        boolean isUsername = passwordResetBody.getId().matches("^[a-zA-Z_]{4,75}$");
        boolean isEmail = passwordResetBody.getId().matches("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+$");

        User user;
        if (isUsername) {
            if (userRepository.findById(passwordResetBody.getId()).isPresent()) {
                user = userRepository.findById(passwordResetBody.getId()).get();
            } else {
                throw new ResourceException("User not found: " + passwordResetBody.getId(), HttpStatus.BAD_REQUEST, httpServletRequest);
            }
        } else if (isEmail) {
            if (userRepository.findAll().stream().anyMatch(usr -> usr.getProfile().getEmail().equals(passwordResetBody.getId()))) {
                user = userRepository.findAll().stream().filter(usr -> usr.getProfile().getEmail().equals(passwordResetBody.getId())).toList().get(0);
            } else {
                throw new ResourceException("User not found: " + passwordResetBody.getId(), HttpStatus.BAD_REQUEST, httpServletRequest);
            }
        } else {
            throw new ResourceException("Invalid ID: " + passwordResetBody.getId(), HttpStatus.BAD_REQUEST, httpServletRequest);
        }

        List<PersistedOtp> otps = otpRepository.findAll().stream()
                .filter(code -> code.getCode().equals(passwordResetBody.getOtp())
                        && code.getForUser().getUsername().equals(user.getUsername())
                        && code.getExpiresOn().isAfter(LocalDateTime.now(ZoneId.systemDefault()))).toList();

        if (otps.size() == 1) {
            String encodedNewPassword = passwordEncoder.encode(passwordResetBody.getNewPassword());
            user.setPassword(encodedNewPassword);

            User savedUser = userRepository.saveAndFlush(user);
            PersistedOtp otp = otps.get(0);
            otpRepository.deleteById(otp.getId());

            this.sendPasswordResetNotification(savedUser.getUsername(), savedUser.getProfile().getEmail());
        } else {
            throw new ResourceException("Invalid OTP", HttpStatus.UNAUTHORIZED, httpServletRequest);
        }
    }

    @Async
    protected void sendPasswordResetNotification(String username, String email) {
        this.executorService.submit(() -> {
            Context context = new Context(Locale.ENGLISH);

            Map<String, Object> mailVariables = new HashMap<>();
            mailVariables.put("datetime", LocalDateTime.now(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a")));
            mailVariables.put("username", username);
            context.setVariables(mailVariables);

            try {
                MimeMessage message = this.javaMailSender.createMimeMessage();
                MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, this.charset);
                messageHelper.setSubject("Password reset successful - InvenTrack");
                messageHelper.setFrom(this.mailFrom);
                messageHelper.setTo(email);

                final String htmlContent = this.templateEngine.process("mail/password-reset.html", context);
                messageHelper.setText(htmlContent, true);
                this.javaMailSender.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
                log.error("Failed to send email", e);
            }
        });
    }
}
