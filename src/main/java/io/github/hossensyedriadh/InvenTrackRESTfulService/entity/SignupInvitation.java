package io.github.hossensyedriadh.InvenTrackRESTfulService.entity;

import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.Authority;
import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.InvitedUserAuthority;
import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.SignupInvitationInvalidationRemarks;
import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.SignupInvitationStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "signup_invitations")
public final class SignupInvitation implements Serializable {
    @Serial
    private static final long serialVersionUID = -2168017203814951262L;

    @Id
    @Setter(AccessLevel.NONE)
    @Column(name = "id", unique = true, updatable = false)
    private String id;

    @Setter(AccessLevel.NONE)
    @Column(name = "token", unique = true, updatable = false, nullable = false)
    private String token;

    @Setter(AccessLevel.NONE)
    @Column(name = "created_on", updatable = false, nullable = false)
    private LocalDateTime createdOn;

    @NotNull(message = "Expiry date can not be null")
    @Column(name = "expires_on", updatable = false, nullable = false)
    private LocalDateTime expiresOn;

    @NotNull(message = "Invitation status can not be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SignupInvitationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "invalidation_remarks", insertable = false)
    private SignupInvitationInvalidationRemarks invalidationRemarks;

    @Column(name = "invalidated_on", insertable = false)
    private LocalDateTime invalidatedOn;

    @NotNull(message = "Recipient email can not be null")
    @Email(message = "Invalid email", regexp = "\\S+@\\S+\\.\\S+")
    @Column(name = "recipient_email", updatable = false, nullable = false)
    private String recipientEmail;

    @NotNull(message = "Invitation authority can not be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "for_authority", updatable = false, nullable = false)
    private InvitedUserAuthority forAuthority;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH}, optional = false)
    @JoinColumn(name = "created_by", referencedColumnName = "username", nullable = false)
    private User createdBy;

    @PrePersist
    private void initialize() {
        String[] uuid = UUID.randomUUID().toString().split("-");
        this.id = uuid[3].concat(uuid[4]);
        this.token = UUID.nameUUIDFromBytes(this.recipientEmail.getBytes(StandardCharsets.UTF_8)).toString()
                .replaceAll("-", "_").concat("_")
                .concat(UUID.randomUUID().toString().replaceAll("-", "_"));
        this.createdOn = LocalDateTime.now();
        this.status = SignupInvitationStatus.VALID;
        this.invalidationRemarks = null;
        this.invalidatedOn = null;
    }
}
