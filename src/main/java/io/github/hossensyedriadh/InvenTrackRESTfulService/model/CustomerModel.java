package io.github.hossensyedriadh.InvenTrackRESTfulService.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@ApiModel(value = "Customer",
        description = "Customer model representing fields for both update and fetch operations")
@Getter
@Setter
public final class CustomerModel implements Serializable {
    @Serial
    private static final long serialVersionUID = -5961709033066798719L;

    @ApiModelProperty(value = "Name of the customer", required = true,
            accessMode = ApiModelProperty.AccessMode.READ_ONLY, example = "John Doe")
    private String name;

    @ApiModelProperty(value = "Phone no. of the customer", required = true,
            accessMode = ApiModelProperty.AccessMode.READ_ONLY, example = "+13456789012")
    private String phone;

    @ApiModelProperty(value = "Email address of the customer",
            accessMode = ApiModelProperty.AccessMode.READ_WRITE, example = "john@test.com")
    private String email;

    @ApiModelProperty(value = "Fully qualified address of the customer", required = true,
            accessMode = ApiModelProperty.AccessMode.READ_WRITE, example = "86 East Bow Ridge Avenue, Monsey, NY 10952")
    private String address;
}
