package io.github.hossensyedriadh.InvenTrackRESTfulService.service.invitations;

import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.Profile;
import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.SignupInvitation;
import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.SignupInvitationInvalidationRemarks;
import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.SignupInvitationStatus;
import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.ResourceCrudException;
import io.github.hossensyedriadh.InvenTrackRESTfulService.mapper.entityToModel.SignupInvitationToModel;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.SignupInvitationModel;
import io.github.hossensyedriadh.InvenTrackRESTfulService.repository.ProfileRepository;
import io.github.hossensyedriadh.InvenTrackRESTfulService.repository.SignupInvitationRepository;
import io.github.hossensyedriadh.InvenTrackRESTfulService.service.CurrentAuthenticationContext;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

@Service
public final class InvitationServiceImpl implements InvitationService {
    private final ObjectFactory<SignupInvitationRepository> invitationRepositoryObjectFactory;
    private final ObjectFactory<ProfileRepository> profileRepositoryObjectFactory;
    private final CurrentAuthenticationContext authenticationContext;
    private final SignupInvitationToModel toModel;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final HttpServletRequest httpServletRequest;

    @Value("${client-application.url.signup-form}")
    private String baseUrl;

    @Value("${spring.mail.default-encoding}")
    private String charset;

    @Value("${accounts.mail.from}")
    private String mailFrom;

    private int invitationValidityDays;

    @Autowired
    public InvitationServiceImpl(ObjectFactory<SignupInvitationRepository> invitationRepositoryObjectFactory,
                                 ObjectFactory<ProfileRepository> profileRepositoryObjectFactory,
                                 CurrentAuthenticationContext authenticationContext,
                                 SignupInvitationToModel toModel,
                                 JavaMailSender mailSender,
                                 TemplateEngine templateEngine,
                                 HttpServletRequest httpServletRequest) {
        this.invitationRepositoryObjectFactory = invitationRepositoryObjectFactory;
        this.profileRepositoryObjectFactory = profileRepositoryObjectFactory;
        this.authenticationContext = authenticationContext;
        this.toModel = toModel;
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.httpServletRequest = httpServletRequest;
    }

    @Value("${application.accounts.signup-invitation.validity}")
    public void setInvitationValidityDays(int invitationValidityDays) {
        this.invitationValidityDays = invitationValidityDays;
    }

    @Override
    public Page<SignupInvitationModel> invitations(Pageable pageable) {
        Page<SignupInvitation> invitationPage = this.invitationRepositoryObjectFactory.getObject().findAll(pageable);
        this.checkAndExpireInvitations();
        return invitationPage.map(toModel::convert);
    }

    @Override
    public Optional<SignupInvitationModel> invitation(String id) {
        this.checkAndExpireInvitations();
        if (this.invitationRepositoryObjectFactory.getObject().findById(id).isPresent()) {
            SignupInvitationModel model = this.toModel.convert(invitationRepositoryObjectFactory.getObject().getById(id));
            return Optional.ofNullable(model);
        }

        return Optional.empty();
    }

    @Override
    public Optional<SignupInvitationModel> createInvitation(SignupInvitationModel signupInvitationModel) {
        this.checkAndExpireInvitations();
        List<SignupInvitation> invitations = invitationRepositoryObjectFactory.getObject().findAll()
                .stream().filter(invitation -> invitation.getRecipientEmail().equals(signupInvitationModel.getRecipientEmail())
                        && invitation.getStatus().equals(SignupInvitationStatus.VALID)).toList();
        if (invitations.size() > 0) {
            throw new ResourceCrudException("Valid invitation for recipient already exists", HttpStatus.BAD_REQUEST,
                    httpServletRequest.getRequestURI());
        } else {
            List<Profile> profiles = profileRepositoryObjectFactory.getObject().findAll()
                    .stream().filter((profile) -> profile.getEmail().equals(signupInvitationModel.getRecipientEmail())).toList();

            if (profiles.size() > 0) {
                throw new ResourceCrudException("Email associated with an account", HttpStatus.BAD_REQUEST,
                        httpServletRequest.getRequestURI());
            }

            SignupInvitation invitation = new SignupInvitation();
            invitation.setRecipientEmail(signupInvitationModel.getRecipientEmail());
            invitation.setForAuthority(signupInvitationModel.getForAuthority());
            invitation.setExpiresOn(LocalDateTime.now().plusDays(this.invitationValidityDays));
            invitation.setCreatedBy(this.authenticationContext.getAuthenticatedUser());

            SignupInvitation createdInvitation = invitationRepositoryObjectFactory.getObject().saveAndFlush(invitation);

            this.sendInvitationThroughEmail(invitation.getRecipientEmail(), invitation.getForAuthority().getSimpleValue(),
                    invitation.getToken(), invitation.getExpiresOn());

            return Optional.ofNullable(this.toModel.convert(createdInvitation));
        }
    }

    private void sendInvitationThroughEmail(String email, String authority, String token, LocalDateTime expiry) {
        Context context = new Context(Locale.ENGLISH);

        Map<String, Object> mailVariables = new HashMap<>();
        mailVariables.put("baseUrl", this.baseUrl);
        mailVariables.put("token", token);
        mailVariables.put("authority", authority);
        String[] expiresOn = expiry.toString().split("T");
        expiresOn[1] = expiresOn[1].split("\\.")[0];
        mailVariables.put("expiry", expiresOn[0].concat(" ").concat(expiresOn[1]));
        context.setVariables(mailVariables);

        try {
            MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, this.charset);
            mimeMessageHelper.setSubject("Signup Invitation - InvenTrack");
            mimeMessageHelper.setFrom(this.mailFrom);
            mimeMessageHelper.setTo(email);

            final String htmlContent = this.templateEngine.process("mail/invitation.html", context);
            mimeMessageHelper.setText(htmlContent, true);

            Thread smtpThread = new Thread(() -> mailSender.send(mimeMessage));
            smtpThread.start();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<SignupInvitationModel> invalidateInvitation(String id) {
        if (invitationRepositoryObjectFactory.getObject().findById(id).isPresent()) {
            SignupInvitation invitation = invitationRepositoryObjectFactory.getObject().getById(id);

            invitation.setStatus(SignupInvitationStatus.INVALID);
            invitation.setInvalidationRemarks(SignupInvitationInvalidationRemarks.REVOKED);
            invitation.setInvalidatedOn(LocalDateTime.now());

            invitationRepositoryObjectFactory.getObject().saveAndFlush(invitation);

            SignupInvitation invalidatedInvitation = invitationRepositoryObjectFactory.getObject().getById(invitation.getId());

            return Optional.ofNullable(this.toModel.convert(invalidatedInvitation));
        }

        return Optional.empty();
    }

    @PostConstruct
    private void checkAndExpireInvitations() {
        List<SignupInvitation> invitations = invitationRepositoryObjectFactory.getObject().findAll()
                .stream().filter(invitation -> invitation.getStatus().equals(SignupInvitationStatus.VALID)
                        && (invitation.getExpiresOn().isBefore(LocalDateTime.now()) || invitation.getExpiresOn().equals(LocalDateTime.now()))).toList();

        List<SignupInvitation> expiredInvitations = new ArrayList<>();

        if (invitations.size() > 0) {
            for (SignupInvitation invitation : invitations) {
                invitation.setStatus(SignupInvitationStatus.INVALID);
                invitation.setInvalidationRemarks(SignupInvitationInvalidationRemarks.EXPIRED);
                invitation.setInvalidatedOn(invitation.getExpiresOn());
                expiredInvitations.add(invitation);
            }
            invitationRepositoryObjectFactory.getObject().saveAllAndFlush(expiredInvitations);
        }
    }
}
