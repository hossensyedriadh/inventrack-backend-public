package io.github.hossensyedriadh.InvenTrackRESTfulService.model;

import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.InvitedUserAuthority;
import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.SignupInvitationInvalidationRemarks;
import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.SignupInvitationStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@ApiModel(value = "SignupInvitation", description = "SignupInvitation model representing fields for both update and fetch operations")
@Getter
@Setter
public final class SignupInvitationModel implements Serializable {
    @Serial
    private static final long serialVersionUID = -2054169904326453270L;

    @ApiModelProperty(value = "ID of the invitation", required = true,
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String id;

    @ApiModelProperty(value = "Timestamp of when the invitation was created",
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private LocalDateTime createdOn;

    @ApiModelProperty(value = "Timestamp of when the invitation will be expired",
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private LocalDateTime expiresOn;

    @ApiModelProperty(value = "Status of the invitation", required = true, allowableValues = "VALID, INVALID",
            accessMode = ApiModelProperty.AccessMode.READ_WRITE)
    private SignupInvitationStatus status;

    @ApiModelProperty(value = "Remarks of invitation invalidation", allowableValues = "USED, EXPIRED, REVOKED",
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private SignupInvitationInvalidationRemarks invalidationRemarks;

    @ApiModelProperty(value = "Timestamp of when the invitation was invalidated",
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private LocalDateTime invalidatedOn;

    @ApiModelProperty(value = "Email of the recipient the invitation to be sent to", required = true,
            accessMode = ApiModelProperty.AccessMode.AUTO)
    private String recipientEmail;

    @ApiModelProperty(value = "Authority of the recipient the invitation to be sent to", required = true,
            allowableValues = "ROLE_ADMINISTRATOR, ROLE_MODERATOR",
            accessMode = ApiModelProperty.AccessMode.AUTO)
    private InvitedUserAuthority forAuthority;

    @ApiModelProperty(value = "Username of the user who created the invitation",
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String createdBy;
}
