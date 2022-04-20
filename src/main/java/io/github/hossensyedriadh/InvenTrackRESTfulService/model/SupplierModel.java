package io.github.hossensyedriadh.InvenTrackRESTfulService.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@ApiModel(value = "Supplier", description = "Supplier model representing fields for both update and fetch operations")
@Getter
@Setter
public final class SupplierModel implements Serializable {
    @Serial
    private static final long serialVersionUID = -1725567599323706165L;

    @ApiModelProperty(value = "Name of the supplier", required = true,
            accessMode = ApiModelProperty.AccessMode.AUTO)
    private String name;

    @ApiModelProperty(value = "Phone no. of the supplier", required = true,
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String phone;

    @ApiModelProperty(value = "Email address of the supplier",
            accessMode = ApiModelProperty.AccessMode.AUTO)
    private String email;

    @ApiModelProperty(value = "Address of the supplier", required = true,
            accessMode = ApiModelProperty.AccessMode.AUTO)
    private String address;

    @ApiModelProperty(value = "Fully qualified website address of the supplier",
            accessMode = ApiModelProperty.AccessMode.AUTO, example = "https://test.com")
    private String website;

    @ApiModelProperty(value = "Notes related to the supplier",
            accessMode = ApiModelProperty.AccessMode.AUTO)
    private String notes;

    @ApiModelProperty(value = "Username of the user who added the supplier",
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private LocalDateTime addedOn;

    @ApiModelProperty(value = "Timestamp of when the supplier was added",
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String addedBy;

    @ApiModelProperty(value = "Username of the user who last time updated the supplier",
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private LocalDateTime updatedOn;

    @ApiModelProperty(value = "Timestamp of when the supplier was updated last time",
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String updatedBy;
}
