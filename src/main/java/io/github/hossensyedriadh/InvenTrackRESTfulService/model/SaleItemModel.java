package io.github.hossensyedriadh.InvenTrackRESTfulService.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@ApiModel(value = "SaleItem", description = "SaleItem model representing fields for both update and fetch operations")
@Getter
@Setter
public final class SaleItemModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 4484079918157059852L;

    @ApiModelProperty(value = "Product for sale", required = true,
            accessMode = ApiModelProperty.AccessMode.AUTO)
    private ProductModel product;

    @ApiModelProperty(value = "Selling Quantity of the product", required = true,
            accessMode = ApiModelProperty.AccessMode.AUTO)
    private int quantity;

    @ApiModelProperty(value = "Price of the product per unit", required = true,
            accessMode = ApiModelProperty.AccessMode.AUTO)
    private double price;
}
