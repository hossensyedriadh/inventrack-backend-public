package io.github.hossensyedriadh.InvenTrackRESTfulService.service.profile;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.transfer.UploadConfiguration;
import com.oracle.bmc.objectstorage.transfer.UploadManager;
import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.Profile;
import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.User;
import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.GenericStatus;
import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.ResourceCrudException;
import io.github.hossensyedriadh.InvenTrackRESTfulService.mapper.entityToModel.UserToUserModel;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.UserModel;
import io.github.hossensyedriadh.InvenTrackRESTfulService.repository.ProfileRepository;
import io.github.hossensyedriadh.InvenTrackRESTfulService.repository.UserRepository;
import io.github.hossensyedriadh.InvenTrackRESTfulService.service.CurrentAuthenticationContext;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public final class ProfileServiceImpl implements ProfileService {
    private final ObjectFactory<UserRepository> userRepositoryObjectFactory;
    private final ObjectFactory<ProfileRepository> profileRepositoryObjectFactory;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final CurrentAuthenticationContext authenticationContext;
    private final UserToUserModel toModel;
    private final HttpServletRequest httpServletRequest;
    @Value("${spring.mail.default-encoding}")
    private String charset;

    @Value("${accounts.mail.from}")
    private String mailFrom;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${oracle.cloud.credentials.config-file-path}")
    private String ociConfigFilePath;

    @Value("${oracle.cloud.credentials.namespace}")
    private String ociNamespace;

    @Value("${oracle.cloud.object-storage.bucket-name}")
    private String ociBucketName;

    @Value("${oracle.cloud.object-storage.avatars-folder-name}")
    private String avatarsFolderName;

    @Autowired
    public ProfileServiceImpl(ObjectFactory<UserRepository> userRepositoryObjectFactory,
                              ObjectFactory<ProfileRepository> profileRepositoryObjectFactory,
                              PasswordEncoder passwordEncoder,
                              JavaMailSender mailSender,
                              TemplateEngine templateEngine,
                              CurrentAuthenticationContext authenticationContext,
                              UserToUserModel toModel,
                              HttpServletRequest httpServletRequest) {
        this.userRepositoryObjectFactory = userRepositoryObjectFactory;
        this.profileRepositoryObjectFactory = profileRepositoryObjectFactory;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.authenticationContext = authenticationContext;
        this.toModel = toModel;
        this.httpServletRequest = httpServletRequest;
    }

    private Boolean passwordMatches(String password) {
        User currentUser = this.authenticationContext.getAuthenticatedUser();

        return passwordEncoder.matches(password, currentUser.getPassword());
    }

    @Override
    public UserModel getUser() {
        return this.toModel.convert(this.authenticationContext.getAuthenticatedUser());
    }

    @Override
    public Boolean isPasswordValid(String password) {
        User currentUser = this.authenticationContext.getAuthenticatedUser();
        return passwordEncoder.matches(password, currentUser.getPassword());
    }

    @Override
    public UserModel updateProfile(UserModel userModel) {
        User user = this.authenticationContext.getAuthenticatedUser();

        String previousEmail = user.getProfile().getEmail();
        user.getProfile().setEmail(userModel.getProfile().getEmail());
        user.getProfile().setPhoneNo(userModel.getProfile().getPhone());
        userRepositoryObjectFactory.getObject().saveAndFlush(user);

        User updatedUser = this.authenticationContext.getAuthenticatedUser();

        if (!previousEmail.equals(updatedUser.getProfile().getEmail())) {
            this.sendEmailUpdateNotification(updatedUser, previousEmail, updatedUser.getProfile().getEmail());
        }

        return this.toModel.convert(updatedUser);
    }

    private void sendEmailUpdateNotification(User currentUser, String currentEmail, String newEmail) {
        Context context = new Context(Locale.ENGLISH);
        String[] localDateTime = LocalDateTime.now().toString().split("T");

        Map<String, Object> mailVariables = new HashMap<>();
        mailVariables.put("datetime", localDateTime[0].concat(" ").concat(localDateTime[1].split("\\.")[0]));
        mailVariables.put("username", currentUser.getUsername());
        mailVariables.put("updatedEmail", newEmail);
        context.setVariables(mailVariables);

        try {
            MimeMessage mimeMessage1 = this.mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper1 = new MimeMessageHelper(mimeMessage1, true, this.charset);
            mimeMessageHelper1.setSubject("Email updated - InvenTrack");
            mimeMessageHelper1.setFrom(this.mailFrom);
            mimeMessageHelper1.setTo(currentEmail);

            final String mailToOldAddress = this.templateEngine.process("mail/email-update-old-address.html", context);
            mimeMessageHelper1.setText(mailToOldAddress, true);

            Thread mailThread1 = new Thread(() -> mailSender.send(mimeMessage1));

            MimeMessage mimeMessage2 = this.mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper2 = new MimeMessageHelper(mimeMessage1, true, this.charset);
            mimeMessageHelper2.setSubject("Email updated - InvenTrack");
            mimeMessageHelper2.setFrom(this.mailFrom);
            mimeMessageHelper2.setTo(newEmail);

            final String mailToNewAddress = this.templateEngine.process("mail/email-update-new-address.html", context);
            mimeMessageHelper2.setText(mailToNewAddress, true);

            Thread mailThread2 = new Thread(() -> mailSender.send(mimeMessage2));

            mailThread1.start();
            mailThread2.start();
        } catch (MessagingException e) {
            throw new ResourceCrudException((e.getCause() != null) ? e.getCause().getMessage() : e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR, httpServletRequest.getRequestURI());
        }
    }

    @Override
    public GenericStatus changePassword(String currentPassword, String newPassword) {
        User currentUser = this.authenticationContext.getAuthenticatedUser();

        if (this.passwordMatches(currentPassword)) {
            String encodedPassword = passwordEncoder.encode(newPassword);
            currentUser.setPassword(encodedPassword);
            userRepositoryObjectFactory.getObject().saveAndFlush(currentUser);

            if (userRepositoryObjectFactory.getObject().getById(currentUser.getUsername()).getPassword().equals(encodedPassword)) {
                this.sendPasswordChangeNotification(currentUser.getUsername(), currentUser.getProfile().getEmail());
                return GenericStatus.SUCCESSFUL;
            } else {
                return GenericStatus.FAILED;
            }
        }

        throw new ResourceCrudException("Wrong password", HttpStatus.BAD_REQUEST, httpServletRequest.getRequestURI());
    }

    private void sendPasswordChangeNotification(String username, String email) {
        Context context = new Context(Locale.ENGLISH);
        String[] localDateTime = LocalDateTime.now().toString().split("T");

        Map<String, Object> mailVariables = new HashMap<>();
        mailVariables.put("datetime", localDateTime[0].concat(" ").concat(localDateTime[1].split("\\.")[0]));
        mailVariables.put("username", username);
        context.setVariables(mailVariables);

        try {
            MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, this.charset);
            mimeMessageHelper.setSubject("Password changed - InvenTrack");
            mimeMessageHelper.setFrom(this.mailFrom);
            mimeMessageHelper.setTo(email);

            final String mailToOldAddress = this.templateEngine.process("mail/password-change.html", context);
            mimeMessageHelper.setText(mailToOldAddress, true);

            Thread mailThread = new Thread(() -> mailSender.send(mimeMessage));
            mailThread.start();

        } catch (MessagingException e) {
            throw new ResourceCrudException((e.getCause() != null) ? e.getCause().getMessage() : e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR, httpServletRequest.getRequestURI());
        }
    }

    @Override
    public GenericStatus changeAvatar(MultipartFile multipartFile) {
        Logger logger = Logger.getLogger(this.getClass());
        String objectName = this.authenticationContext.getAuthenticatedUser().getUsername();

        HashMap<String, String> metadata = new HashMap<>();
        metadata.put("Username", this.authenticationContext.getAuthenticatedUser().getUsername());
        metadata.put("Timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")));
        metadata.put("Copyright", "© " + LocalDate.now().getYear() + ", " + applicationName);

        String contentType = multipartFile.getContentType();

        try {
            File file = new File(System.getProperty("java.io.tmpdir") + "/" + objectName);
            multipartFile.transferTo(file);

            final ConfigFileReader.ConfigFile configFile = ConfigFileReader.parse(ociConfigFilePath);
            final ConfigFileAuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configFile);
            ObjectStorage client = new ObjectStorageClient(provider);
            client.setRegion(Region.AP_SINGAPORE_1);

            UploadConfiguration uploadConfiguration = UploadConfiguration.builder()
                    .allowMultipartUploads(true).allowParallelUploads(false).build();

            UploadManager uploadManager = new UploadManager(client, uploadConfiguration);

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucketName(ociBucketName).namespaceName(ociNamespace).objectName(avatarsFolderName + "/" + objectName)
                    .contentType(contentType).opcMeta(metadata).build();

            UploadManager.UploadRequest uploadRequest = UploadManager.UploadRequest.builder(file).allowOverwrite(true).build(request);
            UploadManager.UploadResponse response = uploadManager.upload(uploadRequest);

            logger.info(response);

            assert objectName != null;
            String staticUrl = client.getEndpoint().concat("/n/").concat(ociNamespace).concat("/b/").concat(ociBucketName)
                    .concat("/o/").concat(avatarsFolderName).concat("/").concat(objectName);

            Profile profile = this.authenticationContext.getAuthenticatedUser().getProfile();
            profile.setAvatar(staticUrl);
            profileRepositoryObjectFactory.getObject().saveAndFlush(profile);

            return GenericStatus.SUCCESSFUL;
        } catch (IOException e) {
            logger.error(e);
        }

        return GenericStatus.FAILED;
    }
}
