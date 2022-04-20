package io.github.hossensyedriadh.InvenTrackRESTfulService.model;

import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.OrderStatus;
import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.PaymentStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@ApiModel(value = "Sale", description = "Sale model representing fields for both update and fetch operations")
@Getter
@Setter
public final class SaleModel implements Serializable {
    @Serial
    private static final long serialVersionUID = -2032435554903038888L;

    @ApiModelProperty(value = "ID of the order", required = true,
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String id;

    @ApiModelProperty(value = "List of products defined as SaleItemModel", required = true,
            accessMode = ApiModelProperty.AccessMode.READ_WRITE)
    private List<SaleItemModel> products;

    @ApiModelProperty(value = "Total amount payable", required = true,
            accessMode = ApiModelProperty.AccessMode.READ_WRITE)
    private Double totalPayable;

    @ApiModelProperty(value = "Total due amount", required = true,
            accessMode = ApiModelProperty.AccessMode.READ_WRITE)
    private Double totalDue;

    @ApiModelProperty(value = "Customer information defined in CustomerModel", required = true,
            accessMode = ApiModelProperty.AccessMode.READ_WRITE)
    private CustomerModel customer;

    @ApiModelProperty(value = "Status of payment", required = true,
            accessMode = ApiModelProperty.AccessMode.READ_WRITE)
    private PaymentStatus paymentStatus;

    @ApiModelProperty(value = "Method of payment", required = true,
            accessMode = ApiModelProperty.AccessMode.READ_WRITE)
    private String paymentMethod;

    @ApiModelProperty(value = "Details of payment",
            accessMode = ApiModelProperty.AccessMode.READ_WRITE)
    private String paymentDetails;

    @ApiModelProperty(value = "Status of order", required = true,
            accessMode = ApiModelProperty.AccessMode.READ_WRITE)
    private OrderStatus orderStatus;

    @ApiModelProperty(value = "Medium of delivery", required = true,
            accessMode = ApiModelProperty.AccessMode.READ_WRITE)
    private String deliveryMedium;

    @ApiModelProperty(value = "Notes regarding the order",
            accessMode = ApiModelProperty.AccessMode.AUTO)
    private String notes;

    @ApiModelProperty(value = "Username of the user who added the order",
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String addedBy;

    @ApiModelProperty(value = "Timestamp of when the order was added",
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private LocalDateTime addedOn;

    @ApiModelProperty(value = "Username of the user who last time updated the order",
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String updatedBy;

    @ApiModelProperty(value = "Timestamp of when the order was updated last time",
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private LocalDateTime updatedOn;
}
