package io.github.hossensyedriadh.inventrackrestfulservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.hossensyedriadh.inventrackrestfulservice.configuration.datasource.PostgreSQLEnumType;
import io.github.hossensyedriadh.inventrackrestfulservice.enumerator.InvitationInvalidationRemarks;
import io.github.hossensyedriadh.inventrackrestfulservice.enumerator.InvitationStatus;
import io.github.hossensyedriadh.inventrackrestfulservice.enumerator.InvitedUserAuthority;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "SignupInvitation")
@Table(name = "signup_invitations", schema = "inventrack")
@TypeDefs(value = {
        @TypeDef(name = "pgsql_signup_invitation_status_enum", typeClass = PostgreSQLEnumType.class),
        @TypeDef(name = "pgsql_signup_invalidation_remarks_enum", typeClass = PostgreSQLEnumType.class),
        @TypeDef(name = "pgsql_invited_user_authority_enum", typeClass = PostgreSQLEnumType.class)
})
public final class SignupInvitation implements Serializable {
    @Serial
    private static final long serialVersionUID = 1897349530890701013L;

    @Id
    @Setter(AccessLevel.NONE)
    @Column(name = "id", unique = true, updatable = false)
    private String id;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @Column(name = "token", unique = true, updatable = false, nullable = false)
    private String token;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Setter(AccessLevel.NONE)
    @Column(name = "created_on", updatable = false, nullable = false)
    private LocalDateTime createdOn;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @NotNull
    @Column(name = "expires_on", updatable = false, nullable = false)
    private LocalDateTime expiresOn;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_signup_invitation_status_enum")
    @Column(name = "status", nullable = false)
    private InvitationStatus status;

    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_signup_invalidation_remarks_enum")
    @Column(name = "invalidation_remarks", insertable = false)
    private InvitationInvalidationRemarks invalidationRemarks;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "invalidated_on", insertable = false)
    private LocalDateTime invalidatedOn;

    @NotNull
    @Email(message = "Invalid email address", regexp = "^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+$")
    @Column(name = "recipient_email", updatable = false, nullable = false)
    private String recipientEmail;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_invited_user_authority_enum")
    @Column(name = "for_authority", updatable = false, nullable = false)
    private InvitedUserAuthority forAuthority;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
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
        this.createdOn = LocalDateTime.now(ZoneId.systemDefault());
        this.status = InvitationStatus.VALID;
        this.invalidationRemarks = null;
        this.invalidatedOn = null;
    }
}
