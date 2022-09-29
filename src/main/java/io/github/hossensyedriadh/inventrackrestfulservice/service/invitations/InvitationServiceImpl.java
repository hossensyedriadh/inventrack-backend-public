package io.github.hossensyedriadh.inventrackrestfulservice.service.invitations;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.Profile;
import io.github.hossensyedriadh.inventrackrestfulservice.entity.SignupInvitation;
import io.github.hossensyedriadh.inventrackrestfulservice.enumerator.InvitationInvalidationRemarks;
import io.github.hossensyedriadh.inventrackrestfulservice.enumerator.InvitationStatus;
import io.github.hossensyedriadh.inventrackrestfulservice.exception.ResourceException;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.ProfileRepository;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.SignupInvitationRepository;
import io.github.hossensyedriadh.inventrackrestfulservice.service.CurrentAuthenticationContext;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j
@Service
public class InvitationServiceImpl implements InvitationService {
    private final SignupInvitationRepository signupInvitationRepository;
    private final ProfileRepository profileRepository;
    private final CurrentAuthenticationContext currentAuthenticationContext;
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine springTemplateEngine;
    private final HttpServletRequest httpServletRequest;
    private final ExecutorService executorService;

    @Autowired
    public InvitationServiceImpl(SignupInvitationRepository signupInvitationRepository, ProfileRepository profileRepository,
                                 CurrentAuthenticationContext currentAuthenticationContext, JavaMailSender javaMailSender,
                                 SpringTemplateEngine springTemplateEngine, HttpServletRequest httpServletRequest) {
        this.signupInvitationRepository = signupInvitationRepository;
        this.profileRepository = profileRepository;
        this.currentAuthenticationContext = currentAuthenticationContext;
        this.javaMailSender = javaMailSender;
        this.springTemplateEngine = springTemplateEngine;
        this.httpServletRequest = httpServletRequest;

        this.executorService = Executors.newSingleThreadExecutor();
    }

    @Value("${client-application.signup-form-url}")
    private String baseUrl;

    @Value("${spring.mail.default-encoding}")
    private String charset;

    @Value("${accounts.mail.from}")
    private String mailFrom;

    private int invitationValidityHours;

    @Value("${accounts.signup-invitation.validity}")
    public void setInvitationValidityHours(int invitationValidityHours) {
        this.invitationValidityHours = invitationValidityHours;
    }

    @Async
    @PostConstruct
    protected void checkAndExpireInvitations() {
        this.executorService.submit(() -> {
            List<SignupInvitation> invitations = signupInvitationRepository.findAll()
                    .stream().filter(invitation -> invitation.getStatus().equals(InvitationStatus.VALID)
                            && (invitation.getExpiresOn().isBefore(LocalDateTime.now(ZoneId.systemDefault()))
                            || invitation.getExpiresOn().equals(LocalDateTime.now(ZoneId.systemDefault())))).toList();

            List<SignupInvitation> expiredInvitations = new ArrayList<>();

            if (invitations.size() > 0) {
                for (SignupInvitation invitation : invitations) {
                    invitation.setStatus(InvitationStatus.INVALID);
                    invitation.setInvalidationRemarks(InvitationInvalidationRemarks.EXPIRED);
                    invitation.setInvalidatedOn(invitation.getExpiresOn());
                    expiredInvitations.add(invitation);
                }
                signupInvitationRepository.saveAllAndFlush(expiredInvitations);
            }
        });
    }

    @Override
    public Page<SignupInvitation> invitations(Pageable pageable) {
        this.checkAndExpireInvitations();
        return signupInvitationRepository.findAll(pageable);
    }

    @Override
    public SignupInvitation invitation(String id) {
        this.checkAndExpireInvitations();

        if (this.signupInvitationRepository.findById(id).isPresent()) {
            return this.signupInvitationRepository.findById(id).get();
        }

        throw new ResourceException("No invitation found with ID: " + id, HttpStatus.BAD_REQUEST, httpServletRequest);
    }

    @Override
    public SignupInvitation create(SignupInvitation signupInvitation) {
        this.checkAndExpireInvitations();

        List<SignupInvitation> invitations = this.signupInvitationRepository.findAll().stream()
                .filter(invitation -> invitation.getRecipientEmail().equals(signupInvitation.getRecipientEmail())
                        && invitation.getStatus().equals(InvitationStatus.VALID)).toList();

        if (invitations.size() > 0) {
            throw new ResourceException("Valid invitation for recipient already exists", HttpStatus.BAD_REQUEST,
                    httpServletRequest);
        }

        List<Profile> profiles = this.profileRepository.findAll().stream()
                .filter(profile -> profile.getEmail().equals(signupInvitation.getRecipientEmail())).toList();

        if (profiles.size() > 0) {
            throw new ResourceException("User exists with the defined email", HttpStatus.BAD_REQUEST, httpServletRequest);
        }

        signupInvitation.setExpiresOn(LocalDateTime.now(ZoneId.systemDefault()).plusHours(this.invitationValidityHours));
        signupInvitation.setCreatedBy(this.currentAuthenticationContext.getAuthenticatedUser());

        SignupInvitation createdInvitation = this.signupInvitationRepository.saveAndFlush(signupInvitation);

        this.sendInvitationEmail(signupInvitation);

        return createdInvitation;
    }

    @Override
    public SignupInvitation invalidate(String id) {
        this.checkAndExpireInvitations();

        if (this.signupInvitationRepository.findById(id).isPresent()) {
            SignupInvitation invitation = this.signupInvitationRepository.findById(id).get();
            invitation.setStatus(InvitationStatus.INVALID);
            invitation.setInvalidationRemarks(InvitationInvalidationRemarks.REVOKED);
            invitation.setInvalidatedOn(LocalDateTime.now(ZoneId.systemDefault()));

            return this.signupInvitationRepository.saveAndFlush(invitation);
        }

        throw new ResourceException("No invitation found with ID: " + id, HttpStatus.BAD_REQUEST, httpServletRequest);
    }

    @Async
    protected void sendInvitationEmail(SignupInvitation invitation) {
        this.executorService.submit(() -> {
            Context context = new Context(Locale.ENGLISH);
            String expiryTimeStamp = invitation.getExpiresOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a"));

            Map<String, Object> mailVariables = new HashMap<>();
            mailVariables.put("baseUrl", this.baseUrl);
            mailVariables.put("token", invitation.getToken());
            mailVariables.put("authority", invitation.getForAuthority().getValue());
            mailVariables.put("expiry", expiryTimeStamp);
            context.setVariables(mailVariables);

            try {
                MimeMessage message = this.javaMailSender.createMimeMessage();
                MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, this.charset);
                messageHelper.setSubject("Signup Invitation - Inventrack");
                messageHelper.setFrom(this.mailFrom);
                messageHelper.setTo(invitation.getRecipientEmail());

                final String htmlContent = this.springTemplateEngine.process("mail/invitation.html", context);
                messageHelper.setText(htmlContent, true);
                this.javaMailSender.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
                log.error("Failed to send email", e);
            }
        });
    }
}
