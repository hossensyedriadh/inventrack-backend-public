package io.github.hossensyedriadh.InvenTrackRESTfulService.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@ApiModel(value = "Product",
        description = "Product model representing fields for both update and fetch operations")
@Getter
@Setter
public final class ProductModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 8636613790951681708L;

    @ApiModelProperty(value = "ID of the product", required = true,
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String id;

    @ApiModelProperty(value = "Name of the product")
    private String name;

    @ApiModelProperty(value = "Category of the product")
    private String category;

    @ApiModelProperty(value = "Specifications of the product",
            accessMode = ApiModelProperty.AccessMode.READ_WRITE)
    private String specifications;

    @ApiModelProperty(value = "Stock available")
    private Integer stock;

    @ApiModelProperty(value = "Price of the product", required = true,
            accessMode = ApiModelProperty.AccessMode.READ_WRITE)
    private Double price;

    @ApiModelProperty(value = "Images of the product",
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private List<String> images;

    @ApiModelProperty(name = "Username of the user last time the product was updated by",
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String updatedBy;

    @ApiModelProperty(name = "Timestamp of when the product was last updated",
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private LocalDateTime updatedOn;

    @ApiModelProperty(value = "Purchase Order information",
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private PurchaseOrderModel purchaseOrder;
}