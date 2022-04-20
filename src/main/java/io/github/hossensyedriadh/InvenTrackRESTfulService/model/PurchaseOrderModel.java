package io.github.hossensyedriadh.InvenTrackRESTfulService.model;

import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.ProductPurchaseStatus;
import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.PurchaseOrderType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@ApiModel(value = "PurchaseOrder",
        description = "PurchaseOrder model representing fields for both update and fetch operations")
@Getter
@Setter
public final class PurchaseOrderModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 3667501276031514987L;

    @ApiModelProperty(value = "ID of the order", required = true,
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String id;

    @ApiModelProperty(value = "Name of the product", required = true,
            accessMode = ApiModelProperty.AccessMode.AUTO)
    private String productName;

    @ApiModelProperty(value = "Category of the product", required = true,
            accessMode = ApiModelProperty.AccessMode.AUTO)
    private String category;

    @ApiModelProperty(value = "Specifications of the product", required = true,
            accessMode = ApiModelProperty.AccessMode.READ_WRITE)
    private String specifications;

    @ApiModelProperty(value = "Quantity of the product ordered", required = true,
            accessMode = ApiModelProperty.AccessMode.AUTO)
    private Integer quantity;

    @ApiModelProperty(value = "Total purchase price of the products", required = true,
            accessMode = ApiModelProperty.AccessMode.AUTO)
    private Double totalPurchasePrice;

    @ApiModelProperty(value = "Shipping cost of the products", required = true,
            accessMode = ApiModelProperty.AccessMode.AUTO)
    private Double shippingCosts;

    @ApiModelProperty(value = "Other costs of the products", required = true,
            accessMode = ApiModelProperty.AccessMode.AUTO)
    private Double otherCosts;

    @ApiModelProperty(value = "Selling price of each product", required = true,
            accessMode = ApiModelProperty.AccessMode.AUTO)
    private Double sellingPricePerUnit;

    @ApiModelProperty(value = "Supplier of product", required = true,
            accessMode = ApiModelProperty.AccessMode.AUTO)
    private SupplierModel supplier;

    @ApiModelProperty(value = "Status of the order", required = true,
            accessMode = ApiModelProperty.AccessMode.AUTO)
    private ProductPurchaseStatus status;

    @ApiModelProperty(value = "Type of the Order", required = true,
            accessMode = ApiModelProperty.AccessMode.AUTO)
    private PurchaseOrderType orderType;

    @ApiModelProperty(value = "Product ID of the product for which restock is requested",
            accessMode = ApiModelProperty.AccessMode.AUTO)
    private String productId;

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
